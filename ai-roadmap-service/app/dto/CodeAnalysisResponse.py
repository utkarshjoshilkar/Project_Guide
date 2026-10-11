from pydantic import BaseModel
from typing import List

class CodeAnalysisResponse(BaseModel):
    filePath: str
    language: str
    classes: List[str]
    methods: List[str]
    imports: List[str]
