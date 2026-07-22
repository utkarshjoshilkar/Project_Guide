from dotenv import load_dotenv
import os

# Load variables from .env file
load_dotenv()


class Settings:
    def __init__(self):
        self.GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")


settings = Settings()