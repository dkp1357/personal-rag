package rag.backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import rag.backend.entity.Document;

@Service
public class RagClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${rag_client.base_url}")
    private final String BASE_URL = "";

    public void processDocument(Document document) {
        String url = BASE_URL + "/embed";

        Map<String, Object> body = new HashMap<>();
        body.put("documentId", document.getId().toString());
        body.put("filePath", document.getFilePath());

        restTemplate.postForObject(url, body, String.class);
    }

    public String query(UUID userId, String query) {
        String url = BASE_URL + "/query";

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId.toString());
        body.put("query", query);

        return restTemplate.postForObject(url, body, String.class);
    }

    public void deleteDocument(Document document) {
        String url = BASE_URL + "/delete/" + document.getId().toString();
        restTemplate.delete(url);
    }
}
