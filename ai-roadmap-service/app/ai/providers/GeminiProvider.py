import time
import logging
from google import genai
from google.genai import errors
from app.config.settings import settings
from app.exception.AIException import AIException

logger = logging.getLogger(__name__)

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
        max_retries = 3
        base_delay = 2.0

        for attempt in range(max_retries):
            try:
                response = self.client.models.generate_content(
                    model="gemini-3.6-flash",
                    contents=prompt
                )
                return response.text
            except errors.APIError as e:
                code = getattr(e, 'code', None)
                message = getattr(e, 'message', str(e))
                # Do not retry on non-transient 4xx errors (except 429 Too Many Requests)
                if code and 400 <= code < 500 and code != 429:
                    logger.error(f"Non-transient Gemini API Error ({code}): {message}")
                    raise AIException(f"Gemini API Client Error ({code}): {message}")
                
                logger.warning(f"Gemini API transient error {code} (attempt {attempt + 1}/{max_retries}): {message}")
                if attempt == max_retries - 1:
                    raise AIException(f"Gemini API unavailable after {max_retries} attempts. Last error: {message}")
                
                time.sleep(base_delay * (2 ** attempt))
            except Exception as e:
                logger.warning(f"Unexpected error calling Gemini (attempt {attempt + 1}/{max_retries}): {e}")
                if attempt == max_retries - 1:
                    raise AIException(f"Failed to communicate with AI service: {str(e)}")
                
                time.sleep(base_delay * (2 ** attempt))
