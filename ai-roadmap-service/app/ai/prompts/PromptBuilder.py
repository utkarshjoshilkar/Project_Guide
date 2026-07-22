from pathlib import Path

from app.ai.prompts.SchemaGenerator import SchemaGenerator
from app.dto.RoadmapRequest import RoadmapRequest


class PromptBuilder:

    def __init__(self):
        self.schema_generator = SchemaGenerator()

        prompt_path = (
            Path(__file__).parent /
            "roadmap_prompt.txt"
        )

        with open(prompt_path, "r", encoding="utf-8") as file:
            self.prompt_template = file.read()

    def build_prompt(self, request: RoadmapRequest) -> str:

        prompt = self.prompt_template

        placeholders = {
            "{project_name}": request.project_name,
            "{project_description}": request.project_description,
            "{branch}": request.branch,
            "{year}": request.year,
            "{experience_level}": request.experience_level,
            "{current_skills}": ", ".join(request.current_skills),
            "{timeline}": request.timeline,
            "{weekly_hours}": str(request.weekly_hours),
            "{learning_goal}": request.learning_goal
        }

        for placeholder, value in placeholders.items():
            prompt = prompt.replace(placeholder, value)

        schema = self.schema_generator.generate_schema()

        prompt += f"""Output JSON Structure:{schema}"""

        return prompt