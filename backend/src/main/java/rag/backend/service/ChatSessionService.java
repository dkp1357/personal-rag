package rag.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
import rag.backend.exception.CustomException.ResourceNotFoundException;
import rag.backend.repository.ChatSessionRepository;

@Service
@RequiredArgsConstructor
public class ChatSessionService {
    private final ChatSessionRepository chatSessionRepository;

    public ChatSession createSession(User user, String title) {
        ChatSession chatSession = ChatSession.builder()
                .user(user)
                .title(title)
                .build();

        return chatSessionRepository.save(chatSession);
    }

    public List<ChatSession> getUserChatSessions(User user) {
        return chatSessionRepository.findByUser(user);
    }

    public List<ChatSession> getUserChatSessionsOrderByCreatedAtAsc(User user) {
        return chatSessionRepository.findByUserOrderByCreatedAtAsc(user);
    }

    public ChatSession getChatSessionBySessionId(User user, String sessionId) {
        // find session by sessionId
        ChatSession session = chatSessionRepository.findById(UUID.fromString(sessionId))
                .orElseThrow(() -> new ResourceNotFoundException("session not found"));

        // check if session belongs to user
        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("session not found");
        }

        return session;
    }
}
