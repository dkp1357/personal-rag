package rag.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import rag.backend.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
