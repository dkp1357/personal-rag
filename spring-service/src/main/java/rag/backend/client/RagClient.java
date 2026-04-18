package rag.backend.client;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RagClient {

        private final WebClient webClient;

        @Value("${rag_client.base_url}")
        private String RAG_BASE_URL = "";

        public void embedDocument(UUID userId, UUID documentId, String filePath) {
                webClient.post()
                                .uri(RAG_BASE_URL + "/embed")
                                .bodyValue(Map.of(
                                                "user_id", userId.toString(),
                                                "document_id", documentId.toString(),
                                                "file_path", filePath))
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();
        }

        public void deleteDocument(UUID documentId) {
                webClient.delete()
                                .uri(RAG_BASE_URL + "/document/{id}", documentId)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();
        }

        public Map<String, Object> query(UUID userId, String query) {
                return webClient.post()
                                .uri(RAG_BASE_URL + "/query")
                                .bodyValue(Map.of(
                                                "user_id", userId.toString(),
                                                "query", query))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                                })
                                .block();
        }
}
