package rag.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rag.backend.entity.Document;
import rag.backend.entity.User;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    @Query("SELECT d FROM Document d JOIN FETCH d.user WHERE d.user = :user")
    List<Document> findByUser(User user);
}
