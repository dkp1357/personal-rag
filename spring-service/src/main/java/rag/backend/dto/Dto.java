package rag.backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

        @JsonProperty("file_name")
        @NotBlank(message = "File name is required")
        private String fileName;

        @JsonProperty("file_path")
        @NotBlank(message = "File path is required")
        private String filePath;
    }

    @Data
    public static class QueryRequest {

        @NotBlank(message = "Query cannot be empty")
        private String query;

        @JsonProperty("session_id")
        private String sessionId; // existing session's id, optional

        @JsonProperty("session_title")
        private String sessionTitle; // new session's title, optional
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryResponse {

        private String answer;
        private List<String> sources;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("refresh_token")
        private String refreshToken;
    }

    @Data
    public static class AccessTokenRequest {

        @JsonProperty("refresh_token")
        private String refreshToken;
    }

    @Data
    @Builder
    public static class AccessTokenResponse {

        @JsonProperty("access_token")
        private String accessToken;
    }
}
