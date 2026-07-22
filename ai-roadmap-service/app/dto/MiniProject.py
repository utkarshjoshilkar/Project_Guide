from pydantic import BaseModel


class MiniProject(BaseModel):

    name: str
    description: str
    estimated_hours: int