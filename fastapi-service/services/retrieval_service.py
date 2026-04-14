from services.embedding_service import embed_texts
from store.faiss_store import load_or_create_index
import numpy as np


def retrieve(user_id, query, k=3):

    index = load_or_create_index(user_id)

    query_vec = embed_texts([query])

    distances, indices = index.search(np.array(query_vec), k)

    return indices[0]
