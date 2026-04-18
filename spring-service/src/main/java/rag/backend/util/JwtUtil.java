package rag.backend.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import rag.backend.repository.BlacklistedTokenRepository;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET = "secret-secret-secret-secret-secret-secret"; // min 32 chars

    @Value("${jwt.expiration_in_seconds}")
    private long EXPIRATION_IN_SECONDS = 60 * 60 * 24; // 24 hours

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UUID userId, String email, String role) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + (EXPIRATION_IN_SECONDS * 1000));

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId.toString())
                .claim("role", role)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaims(token).get("userId", String.class));
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        if (blacklistedTokenRepository.existsByToken(token)) {
            return false;
        }

        try {
            extractClaims(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }
}
