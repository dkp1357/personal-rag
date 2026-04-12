package rag.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import rag.backend.entity.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, UUID> {
    boolean existsByToken(String token);
}
