from sentence_transformers import SentenceTransformer

from config import config

model = SentenceTransformer(config.EMBEDDING_MODEL)


def embed_texts(texts):
    return model.encode(texts, batch_size=32)
