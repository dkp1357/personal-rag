package rag.backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rag.backend.dto.ApiResponse;
import rag.backend.dto.Dto.AccessTokenRequest;
import rag.backend.dto.Dto.AccessTokenResponse;
import rag.backend.dto.Dto.AuthResponse;
import rag.backend.dto.Dto.LoginRequest;
import rag.backend.dto.Dto.RegisterRequest;
import rag.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("user registered successfully, please login", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("login successful", response));
    }

    @PostMapping("new-access-token")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> accessToken(
            @RequestBody AccessTokenRequest request) {

        AccessTokenResponse response = authService.getNewAccessToken(request);

        return ResponseEntity.ok(
                ApiResponse.success("access token refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String accessToken = authHeader.substring(7);

        authService.logout(accessToken);

        return ResponseEntity.ok(ApiResponse.success("logged out successfully", null));
    }
}
