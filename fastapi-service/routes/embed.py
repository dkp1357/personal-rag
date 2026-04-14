from fastapi import APIRouter
import numpy as np

from utils.file_parser import parse_file
from utils.chunking import chunk_text
from services.embedding_service import embed_texts
from models.schemas import EmbedRequest
from store.faiss_store import load_or_create_index, save_index
from store.metadata_store import load_metadata, save_metadata

router = APIRouter()


@router.post("/embed")
def embed_document(req: EmbedRequest):

    text = parse_file(req.file_path)
    chunks = chunk_text(text)

    embeddings = embed_texts(chunks)

    index = load_or_create_index(req.user_id)
    metadata = load_metadata(req.user_id)

    base_id = len(metadata["chunks"])

    new_entries = []

    for i, chunk in enumerate(chunks):
        chunk_id = base_id + i

        new_entries.append(
            {"id": chunk_id, "document_id": req.documentId, "text": chunk}
        )

    index.add(np.array(embeddings))

    metadata["chunks"].extend(new_entries)

    save_index(req.user_id, index)

    save_metadata(req.user_id, metadata)

    return {"status": "success", "chunks_added": len(chunks)}
