from app.ai.providers.GeminiProvider import GeminiProvider
from app.ai.parser.ResponseParser import ResponseParser
from app.dto.RoadmapResponse import RoadmapResponse


class LLMService:

    def __init__(self):
        self.provider = GeminiProvider()
        self.parser = ResponseParser()

    def generate_response(self, prompt: str) -> RoadmapResponse:

        response = self.provider.generate_content(prompt)

        return self.parser.parse(response)