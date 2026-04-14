package rag.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
import java.util.List;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByUser(User user);

    List<ChatSession> findByUserOrderByCreatedAtAsc(User user);
}
