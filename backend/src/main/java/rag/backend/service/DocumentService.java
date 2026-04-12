package rag.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.entity.Document;
import rag.backend.entity.User;
import rag.backend.repository.DocumentRepository;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RagClientService ragClientService;

    public Document saveDocument(Document document) {
        Document saved = documentRepository.save(document);

        ragClientService.processDocument(document);

        return saved;
    }

    public List<Document> getUserDocuments(User user) {
        return documentRepository.findByUser(user);
    }

    public void deleteDocument(Document document) {
        documentRepository.delete(document);

        ragClientService.deleteDocument(document);
    }

}
