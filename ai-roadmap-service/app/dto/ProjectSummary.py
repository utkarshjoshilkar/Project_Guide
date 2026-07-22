from pydantic import BaseModel


class ProjectSummary(BaseModel):

    project_name: str
    description: str
    duration: str
    weekly_effort: str