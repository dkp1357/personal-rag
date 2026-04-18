from fastapi import FastAPI
from routes import delete_document, embed, query

app = FastAPI()

app.include_router(embed.router)
app.include_router(query.router)
app.include_router(delete_document.router)


@app.get("/")
def root():
    return {"message": "RAG service running"}
