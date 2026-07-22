from pydantic import BaseModel


class Milestone(BaseModel):

    milestone_id: int
    title: str
    target_week: str
    description: str