from pydantic import BaseModel

from app.dto.ProjectSummary import ProjectSummary
from app.dto.Prerequisite import Prerequisite
from app.dto.RoadmapPhase import RoadmapPhase
from app.dto.Technology import Technology
from app.dto.Resource import Resource
from app.dto.MiniProject import MiniProject
from app.dto.Milestone import Milestone
from app.dto.Course import Course
from app.dto.Certification import Certification
from app.dto.FutureEnhancement import FutureEnhancement


class RoadmapResponse(BaseModel):

    project_summary: ProjectSummary

    prerequisites: list[Prerequisite]

    phase_wise_learning_plan: list[RoadmapPhase]

    technologies_to_learn: list[Technology]

    learning_resources: list[Resource]

    mini_projects: list[MiniProject]

    milestones: list[Milestone]

    recommended_courses: list[Course]

    recommended_certifications: list[Certification]

    future_enhancements: list[FutureEnhancement]

    final_expected_outcome: str