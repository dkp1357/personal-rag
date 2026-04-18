package rag.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import rag.backend.entity.Document;
import rag.backend.entity.User;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByUser(User user);
}
