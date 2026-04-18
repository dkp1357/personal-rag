package rag.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rag.backend.dto.ApiResponse;
import rag.backend.dto.Dto.QueryRequest;
import rag.backend.dto.Dto.QueryResponse;
import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
import rag.backend.service.ChatMessageService;
import rag.backend.service.ChatSessionService;
import rag.backend.util.AuthUtil;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;
    private final AuthUtil authUtil;

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<QueryResponse>> query(
            @Valid @RequestBody QueryRequest request) {

        User user = authUtil.getCurrentUser();

        String query = request.getQuery();

        ChatSession session = chatSessionService.getChatSession(user, request);

        QueryResponse response = chatMessageService.handleQuery(user, session, query);

        return ResponseEntity.ok(ApiResponse.success("query processed successfully", response));
    }
}