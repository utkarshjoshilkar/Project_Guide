from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from app.exception.AIException import AIException


def register_exception_handlers(app: FastAPI):

    @app.exception_handler(AIException)
    async def ai_exception_handler(request: Request, exc: AIException):
        return JSONResponse(
            status_code=500,
            content={
                "status": 500,
                "message": exc.message
            }
        )