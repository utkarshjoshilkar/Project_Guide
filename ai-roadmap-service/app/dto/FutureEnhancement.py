from pydantic import BaseModel


class FutureEnhancement(BaseModel):

    feature: str
    details: str