from pydantic import BaseModel
from typing import List


class RoadmapRequest(BaseModel):
    project_name: str
    project_description: str
    branch: str
    year: str
    experience_level: str
    current_skills: List[str]
    timeline: str
    weekly_hours: int
    learning_goal: str