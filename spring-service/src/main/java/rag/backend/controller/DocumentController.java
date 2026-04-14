package rag.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rag.backend.dto.ApiResponse;
import rag.backend.dto.Dto.DocumentRequest;
import rag.backend.entity.Document;
import rag.backend.entity.User;
import rag.backend.service.DocumentService;
import rag.backend.util.AuthUtil;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final AuthUtil authUtil;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Document>> upload(
            @Valid @RequestBody DocumentRequest request) {

        User user = authUtil.getCurrentUser();

        Document document = documentService.upload(request, user);

        return ResponseEntity.ok(
                ApiResponse.success("document uploaded successfully", document));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Document>>> getDocuments() {

        User user = authUtil.getCurrentUser();

        List<Document> documents = documentService.getUserDocuments(user);

        return ResponseEntity.ok(
                ApiResponse.success("documents fetched successfully", documents));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable UUID id) {

        User user = authUtil.getCurrentUser();

        documentService.deleteDocument(id, user);

        return ResponseEntity.ok(ApiResponse.success("document deleted successfully", null));
    }
}