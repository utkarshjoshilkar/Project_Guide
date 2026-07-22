from fastapi import FastAPI
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.controller.RoadmapController import router

app = FastAPI(
    title="Project Guide AI Roadmap Service",
    version="1.0.0"
)

# -----------------------------
# CORS Configuration
# -----------------------------
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://127.0.0.1:5500",
        "http://localhost:5500",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# -----------------------------
# Register Routes
# -----------------------------
app.include_router(router)

@app.get("/")
def home():
    return {"message": "AI Roadmap Service Running"}