from google import genai
from app.config.settings import settings
from app.exception.AIException import AIException

class GeminiProvider:
    def list_models(self):

        models = self.client.models.list()

        for model in models:
            print(model.name)
    def __init__(self):
        self.client = genai.Client(
            api_key=settings.GEMINI_API_KEY
        )

    def generate_content(self, prompt: str) -> str:

        try:

            response = self.client.models.generate_content(
                model="gemini-3.5-flash",
                contents=prompt
            )

            return response.text

        except Exception as e:
            raise AIException(f"Gemini API Error: {str(e)}")
