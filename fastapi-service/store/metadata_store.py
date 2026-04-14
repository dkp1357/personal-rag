import json
import os

from config import DATA_PATH


def get_metadata_path(user_id):
    return os.path.join(DATA_PATH, f"{user_id}_meta.json")


def load_metadata(user_id):
    path = get_metadata_path(user_id)

    if not os.path.exists(path):
        return {"chunks": []}

    with open(path, "r") as f:
        return json.load(f)


def save_metadata(user_id, data):
    with open(get_metadata_path(user_id), "w") as f:
        json.dump(data, f)
