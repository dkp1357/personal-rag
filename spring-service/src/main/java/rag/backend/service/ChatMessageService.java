package rag.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.client.RagClient;
import rag.backend.dto.Dto.QueryResponse;
import rag.backend.entity.ChatMessage;
import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
import rag.backend.mapper.Mapper.QueryResponseMapper;
import rag.backend.repository.ChatMessageRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final RagClient ragClient;

    public QueryResponse handleQuery(User user, ChatSession chatSession, String query) {
        Map<String, Object> response = ragClient.query(user.getId(), query);

        String answer = (String) response.get("answer");

        Object obj = response.get("sources");
        List<String> sources = new ArrayList<>();
        if (obj instanceof List<?>) {
            List<?> tempList = (List<?>) obj;
            for (Object item : tempList) {
                if (item instanceof String) {
                    sources.add((String) item);
                }
            }
        }

        ChatMessage message = ChatMessage.builder()
                .user(user)
                .chatSession(chatSession)
                .query(query)
                .response(answer)
                .build();

        chatMessageRepository.save(message);

        return QueryResponseMapper.toResponse(answer, sources);
    }
}
