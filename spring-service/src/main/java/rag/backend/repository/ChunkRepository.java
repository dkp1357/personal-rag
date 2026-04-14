package rag.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import rag.backend.entity.Chunk;
import rag.backend.entity.Document;

import java.util.List;

public interface ChunkRepository extends JpaRepository<Chunk, UUID> {
    List<Chunk> findByDocument(Document document);
}
