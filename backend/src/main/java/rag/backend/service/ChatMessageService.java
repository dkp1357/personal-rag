package rag.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.dto.Dto.QueryResponse;
import rag.backend.entity.ChatMessage;
import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
import rag.backend.mapper.Mapper.ChatMapper;
import rag.backend.repository.ChatMessageRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final RagClientService ragClientService;

    public QueryResponse handleQuery(User user, ChatSession chatSession, String query) {
        String response = ragClientService.query(user.getId(), query);

        // TODO: later → parse sources from response
        List<String> sources = List.of();

        ChatMessage message = ChatMessage.builder()
                .user(user)
                .chatSession(chatSession)
                .query(query)
                .response(response)
                .build();

        chatMessageRepository.save(message);

        return ChatMapper.toResponse(response, sources);
    }
}
