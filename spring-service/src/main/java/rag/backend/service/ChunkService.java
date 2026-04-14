package rag.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.entity.Chunk;
import rag.backend.entity.Document;
import rag.backend.repository.ChunkRepository;

@Service
@RequiredArgsConstructor
public class ChunkService {

    private final ChunkRepository chunkRepository;

    public void saveAll(List<Chunk> chunks) {
        chunkRepository.saveAll(chunks);
    }

    public List<Chunk> getChunksByDocument(Document document) {
        return chunkRepository.findByDocument(document);
    }
}
