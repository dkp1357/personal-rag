package rag.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rag.backend.client.RagClient;
import rag.backend.entity.Document;
import rag.backend.entity.User;
import rag.backend.exception.CustomException.ResourceNotFoundException;
import rag.backend.exception.CustomException.UnauthorizedException;
import rag.backend.repository.DocumentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RagClient ragClient;

    @Value("${app.upload_dir}")
    private String UPLOAD_DIR = "../uploads/";

    public Document upload(MultipartFile file, User user) {

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(UPLOAD_DIR);

            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);

            file.transferTo(filePath.toFile());

            Document document = Document.builder()
                    .user(user)
                    .fileName(file.getOriginalFilename())
                    .filePath(filePath.toString())
                    .build();
            Document saved = documentRepository.save(document);

            ragClient.embedDocument(user.getId(), saved.getId(), filePath.toString());

            return saved;

        } catch (IllegalStateException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        } catch (IOException e) {
            throw new RuntimeException("file upload failed:" + e.getLocalizedMessage());
        }
    }

    public List<Document> getUserDocuments(User user) {
        return documentRepository.findByUser(user);
    }

    public void deleteDocument(UUID documentId, User user) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized access to document");
        }

        File file = new File(document.getFilePath());
        file.delete();

        documentRepository.delete(document);
        ragClient.deleteDocument(documentId);
    }
}
