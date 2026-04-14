from pydantic import BaseModel


class EmbedRequest(BaseModel):
    user_id: str
    document_id: str
    file_path: str


class QueryRequest(BaseModel):
    user_id: str
    query: str
