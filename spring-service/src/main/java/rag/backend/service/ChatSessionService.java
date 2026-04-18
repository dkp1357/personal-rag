package rag.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.dto.Dto.QueryRequest;
import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
import rag.backend.exception.CustomException.ResourceNotFoundException;
import rag.backend.repository.ChatSessionRepository;

@Service
@RequiredArgsConstructor
public class ChatSessionService {
    private final ChatSessionRepository chatSessionRepository;

    public ChatSession getChatSession(User user, QueryRequest req) {
        ChatSession session = null;

        if (req.getSessionId() != null) {
            // get session if sessionId is given
            session = this.getChatSessionBySessionId(user, UUID.fromString(req.getSessionId()));
        } else {
            if (req.getSessionTitle() != null) {
                // create session if sessionId is not given but sessionTitle is there
                session = this.createSession(user, req.getSessionTitle());
            } else {
                // create session with title
                session = this.createSession(user, this.generateSessionTitle(req.getQuery()));
            }
        }

        return session;
    }

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

    public ChatSession getChatSessionBySessionId(User user, UUID sessionId) {
        // find session by sessionId
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("session not found"));

        // check if session belongs to user
        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("session not found");
        }

        return session;
    }

    private String generateSessionTitle(String query) {
        if (query == null || query.isBlank()) {
            return "New Chat";
        }

        String trimmed = query.trim();

        if (trimmed.length() > 40) {
            trimmed = trimmed.substring(0, 37) + "...";
        }

        return trimmed;
    }
}
