import numpy as np
from fastapi import APIRouter, Query

from store.faiss_store import load_or_create_index, save_index
from store.metadata_store import load_metadata, save_metadata
from services.embedding_service import embed_texts

router = APIRouter()


@router.delete("/document")
def delete_document(user_id: str = Query(...), document_id: str = Query(...)):
    index = load_or_create_index(user_id)
    metadata = load_metadata(user_id)

    chunks = metadata["chunks"]

    remaining_chunks = [c for c in chunks if c["document_id"] != document_id]

    if len(remaining_chunks) == len(chunks):
        return {"status": "not_found", "message": "Document not found"}

    texts = [c["text"] for c in remaining_chunks]

    if len(texts) == 0:
        index.reset()
    else:
        new_embeddings = embed_texts(texts)
        index.reset()
        index.add(np.array(new_embeddings))

    metadata["chunks"] = remaining_chunks

    save_index(user_id, index)
    save_metadata(user_id, metadata)

    return {"status": "success", "message": f"Deleted document {document_id}"}
