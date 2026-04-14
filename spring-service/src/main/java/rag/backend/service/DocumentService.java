package rag.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.dto.Dto.DocumentRequest;
import rag.backend.entity.Document;
import rag.backend.entity.User;
import rag.backend.exception.CustomException.ResourceNotFoundException;
import rag.backend.exception.CustomException.UnauthorizedException;
import rag.backend.mapper.Mapper.DocumentMapper;
import rag.backend.repository.DocumentRepository;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RagClientService ragClientService;

    public Document upload(DocumentRequest request, User user) {

        Document document = DocumentMapper.toEntity(request, user);
        Document saved = documentRepository.save(document);

        ragClientService.processDocument(saved);

        return saved;
    }

    public List<Document> getUserDocuments(User user) {
        return documentRepository.findByUser(user);
    }

    public void deleteDocument(UUID docId, User user) {

        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized access to document");
        }

        documentRepository.delete(document);
        ragClientService.deleteDocument(docId);
    }
}
