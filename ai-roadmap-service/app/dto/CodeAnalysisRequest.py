from pydantic import BaseModel

class CodeAnalysisRequest(BaseModel):
    filePath: str
    sourceCode: str
