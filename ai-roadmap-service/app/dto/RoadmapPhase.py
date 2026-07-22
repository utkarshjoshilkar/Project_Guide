from pydantic import BaseModel


class RoadmapPhase(BaseModel):

    phase: str
    timeline: str
    weekly_allocation: str
    objectives: list[str]
    topics_to_cover: list[str]
    action_items: list[str]