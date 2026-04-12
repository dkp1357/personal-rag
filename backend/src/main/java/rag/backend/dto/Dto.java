package rag.backend.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public class Dto {

    @Data
    public static class RegisterRequest {

        @Email(message = "invalid email format")
        @NotBlank(message = "email is required")
        private String email;

        @Size(min = 6, message = "Password must be at least 6 characters")
        @NotBlank(message = "password is required")
        private String password;
    }

    @Data
    public static class LoginRequest {

        @Email(message = "invalid email format")
        @NotBlank(message = "email is required")
        private String email;

        @NotBlank(message = "password is required")
        private String password;
    }

    @Data
    public static class DocumentRequest {

        @NotBlank(message = "File name is required")
        private String fileName;

        @NotBlank(message = "File path is required")
        private String filePath;
    }

    @Data
    public static class QueryRequest {

        @NotBlank(message = "Query cannot be empty")
        private String query;

        private String sessionId; // optional
    }

    @Data
    @Builder
    public static class QueryResponse {

        private String answer;
        private List<String> sources;
    }

    @Data
    @Builder
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
    }
}
