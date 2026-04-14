from fastapi import FastAPI
from routes import embed, query

app = FastAPI()

app.include_router(embed.router)
app.include_router(query.router)


@app.get("/")
def root():
    return {"message": "RAG service running"}
