package rag.backend.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import rag.backend.dto.Dto.AuthResponse;
import rag.backend.dto.Dto.LoginRequest;
import rag.backend.dto.Dto.RegisterRequest;
import rag.backend.entity.BlacklistedToken;
import rag.backend.entity.RefreshToken;
import rag.backend.entity.Role;
import rag.backend.entity.User;
import rag.backend.exception.CustomException.*;
import rag.backend.mapper.Mapper.UserMapper;
import rag.backend.repository.BlacklistedTokenRepository;
import rag.backend.repository.RefreshTokenRepository;
import rag.backend.repository.UserRepository;
import rag.backend.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${refresh_token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * 60; // 7 days in seconds

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name());

        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    // get access (jwt) token from refresh token
    public String getAccessToken(String refreshTokenString) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().before(new Date())) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name());
    }

    // blacklist jwt token on logout
    public void logout(String token) {

        Date expiry = jwtUtil.extractClaims(token).getExpiration();

        BlacklistedToken blacklisted = BlacklistedToken.builder()
                .token(token)
                .expiryDate(expiry)
                .build();

        blacklistedTokenRepository.save(blacklisted);
    }

    private RefreshToken createRefreshToken(User user) {

        RefreshToken token = RefreshToken.builder()
                .userId(user.getId())
                .expiryDate(new Date(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRATION * 1000)))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(token);
    }
}