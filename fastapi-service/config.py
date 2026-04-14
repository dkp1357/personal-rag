from pydantic_settings import BaseSettings, SettingsConfigDict


class Config(BaseSettings):
    OPENAI_API_KEY: str = ""
    EMBEDDING_MODEL: str = "all-MiniLM-L6-v2"
    VECTOR_DIM: int = 384
    DATA_PATH: str = "data/"
    GEMINI_API_KEY: str = ""

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")


config = Config()
