from fastapi import APIRouter, HTTPException
from app.dto.CodeAnalysisRequest import CodeAnalysisRequest
from app.dto.CodeAnalysisResponse import CodeAnalysisResponse
from app.service.CodeAnalysisService import CodeAnalysisService

router = APIRouter()
analysis_service = CodeAnalysisService()

@router.post("/api/code-analysis/java", response_model=CodeAnalysisResponse)
def analyze_java_code(request: CodeAnalysisRequest):
    try:
        return analysis_service.analyze_java(request)
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail="Error processing Java code analysis"
        )
