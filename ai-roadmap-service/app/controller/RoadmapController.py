from fastapi import APIRouter

from fastapi import HTTPException
from app.service.RoadmapStorage import RoadmapStorage  

from app.dto.RoadmapResponse import RoadmapResponse
from app.dto.RoadmapRequest import RoadmapRequest
from app.service.RoadmapService import RoadmapService

router = APIRouter()

roadmap_service = RoadmapService()


@router.post("/generate-roadmap",response_model=RoadmapResponse)
def generate_roadmap(request: RoadmapRequest):

    return roadmap_service.generate_roadmap(request)



@router.get("/latest-roadmap", response_model=RoadmapResponse)
def get_latest_roadmap():

    if RoadmapStorage.latest_roadmap is None:
        raise HTTPException(
            status_code=404,
            detail="No roadmap has been generated yet."
        )

    return RoadmapStorage.latest_roadmap