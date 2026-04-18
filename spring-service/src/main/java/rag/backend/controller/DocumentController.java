package rag.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import rag.backend.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<?>> upload(@RequestParam("file") MultipartFile file) {

        User user = authUtil.getCurrentUser();

        Document document = documentService.upload(file, user);

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

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable UUID documentId) {

        User user = authUtil.getCurrentUser();

        documentService.deleteDocument(documentId, user);

        return ResponseEntity.ok(ApiResponse.success("document deleted successfully", null));
    }
}