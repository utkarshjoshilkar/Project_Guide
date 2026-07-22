from pydantic import BaseModel


class Technology(BaseModel):

    name: str
    purpose: str