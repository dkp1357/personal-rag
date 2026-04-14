package rag.backend.mapper;

import rag.backend.entity.*;

import java.util.List;

import rag.backend.dto.Dto.*;

public class Mapper {

    public static class UserMapper {

        public static User toEntity(RegisterRequest registerRquest) {
            return User.builder()
                    .email(registerRquest.getEmail())
                    .password(registerRquest.getPassword())
                    .build();
        }
    }

    public static class DocumentMapper {

        public static Document toEntity(DocumentRequest request, User user) {
            return Document.builder()
                    .fileName(request.getFileName())
                    .filePath(request.getFilePath())
                    .user(user)
                    .build();
        }
    }

    public static class QueryResponseMapper {

        public static QueryResponse toResponse(String answer, List<String> sources) {
            return QueryResponse.builder()
                    .answer(answer)
                    .sources(sources)
                    .build();
        }
    }
}
