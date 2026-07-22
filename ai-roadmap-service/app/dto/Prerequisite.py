from pydantic import BaseModel


class Prerequisite(BaseModel):

    topic: str
    concepts: str