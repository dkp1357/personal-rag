from fastapi import APIRouter

from services.retrieval_service import retrieve
from services.langchain_service import LangChainService
from store.metadata_store import load_metadata
from models.schemas import QueryRequest

router = APIRouter()

llm_service = LangChainService()


@router.post("/query")
def query_rag(req: QueryRequest):

    indices = retrieve(req.user_id, req.query)

    metadata = load_metadata(req.user_id)
    chunks = metadata["chunks"]

    context_chunks = []
    sources = set()

    for i in indices:
        if isinstance(i, int) and 0 <= i < len(chunks):
            context_chunks.append(chunks[i]["text"])
            sources.add(chunks[i]["document_id"])
        else:
            print(f"Skipping invalid index: {i}")

    answer = llm_service.generate(req.query, context_chunks)

    return {"answer": answer, "sources": list(sources)}
