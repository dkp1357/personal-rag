package rag.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
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
}
