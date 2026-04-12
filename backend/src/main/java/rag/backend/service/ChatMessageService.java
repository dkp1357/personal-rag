package rag.backend.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.entity.ChatMessage;
import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
import rag.backend.repository.ChatMessageRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final RagClientService ragClientService;

    public String handleQuery(User user, ChatSession chatSession, String query) {
        String response = ragClientService.query(user.getId(), query);

        ChatMessage message = ChatMessage.builder()
                .user(user)
                .chatSession(chatSession)
                .query(query)
                .response(response)
                .build();

        chatMessageRepository.save(message);

        return response;
    }
}
