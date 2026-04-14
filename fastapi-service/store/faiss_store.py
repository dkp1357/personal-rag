import faiss
import os

from config import config


def get_index_path(user_id):
    return os.path.join(config.DATA_PATH, f"{user_id}.index")


def save_index(user_id, index):
    faiss.write_index(index, get_index_path(user_id=user_id))


def load_or_create_index(user_id):
    path = get_index_path(user_id)

    if os.path.exists(path=path):
        return faiss.read_index(path=path)

    return faiss.IndexFlatL2(config.VECTOR_DIM)
