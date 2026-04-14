package rag.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rag.backend.dto.ApiResponse;
import rag.backend.entity.ChatSession;
import rag.backend.entity.User;
import rag.backend.service.ChatSessionService;
import rag.backend.util.AuthUtil;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ChatSessionController {

    private final ChatSessionService chatSessionService;
    private final AuthUtil authUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatSession>> createSession(
            @RequestParam String title) {

        User user = authUtil.getCurrentUser();

        ChatSession session = chatSessionService.createSession(user, title);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("session created", session));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatSession>>> getSessions() {

        User user = authUtil.getCurrentUser();

        List<ChatSession> sessions = chatSessionService.getUserChatSessions(user);

        return ResponseEntity.ok(ApiResponse.success("sessions fetched", sessions));
    }
}
