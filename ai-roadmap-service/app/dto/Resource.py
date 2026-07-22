from pydantic import BaseModel


class Resource(BaseModel):

    resource_name: str
    url: str
    type: str