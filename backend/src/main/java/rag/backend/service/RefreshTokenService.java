package rag.backend.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.entity.RefreshToken;
import rag.backend.entity.User;
import rag.backend.exception.CustomException.UnauthorizedException;
import rag.backend.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${refresh_token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * 60; // 7 days in seconds

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .userId(user.getId())
                .expiryDate(new Date(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRATION * 1000)))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(token);
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                    .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

            Date currentDate = new Date();

            if (refreshToken.isRevoked() || refreshToken.getExpiryDate().before(currentDate)) {
                throw new UnauthorizedException("Refresh token expired or revoked");
            }

            return true;
        } catch (UnauthorizedException ex) {
            return false;
        }
    }
}
