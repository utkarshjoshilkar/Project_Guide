from app.ai.prompts.PromptBuilder import PromptBuilder
from app.ai.LLMService import LLMService
from app.dto.RoadmapRequest import RoadmapRequest
from app.dto.RoadmapResponse import RoadmapResponse

from app.service.RoadmapStorage import RoadmapStorage


class RoadmapService:

    def __init__(self):
        self.prompt_builder = PromptBuilder()
        self.llm_service = LLMService()

    def generate_roadmap(self, request: RoadmapRequest) -> RoadmapResponse:

        prompt = self.prompt_builder.build_prompt(request)

        roadmap = self.llm_service.generate_response(prompt)

        RoadmapStorage.latest_roadmap = roadmap

        return roadmap