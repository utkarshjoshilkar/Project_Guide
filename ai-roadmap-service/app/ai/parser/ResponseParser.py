import json

from app.dto.RoadmapResponse import RoadmapResponse
from app.exception.AIException import AIException

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


class ResponseParser:

    def parse(self, response: str) -> RoadmapResponse:

        if not response or not response.strip():
            raise AIException("AI returned an empty response.")

        try:
            cleaned_response = response.strip()
            if cleaned_response.startswith("```json"):
                cleaned_response = cleaned_response[7:]
            if cleaned_response.startswith("```"):
                cleaned_response = cleaned_response[3:]
            if cleaned_response.endswith("```"):
                cleaned_response = cleaned_response[:-3]
            cleaned_response = cleaned_response.strip()
            data = json.loads(cleaned_response)

            return RoadmapResponse(
                project_summary=self._create_project_summary(data),
                prerequisites=self._create_prerequisites(data),
                phase_wise_learning_plan=self._create_phases(data),
                technologies_to_learn=self._create_technologies(data),
                learning_resources=self._create_resources(data),
                mini_projects=self._create_mini_projects(data),
                milestones=self._create_milestones(data),
                recommended_courses=self._create_courses(data),
                recommended_certifications=self._create_certifications(data),
                future_enhancements=self._create_future_enhancements(data),
                final_expected_outcome=self._create_final_outcome(data)
             )

        except json.JSONDecodeError:
            raise AIException("AI returned an invalid JSON response.")

        except Exception as e:
            raise AIException(f"Response Parsing Error: {str(e)}")

    def _create_project_summary(self, data: dict) -> ProjectSummary:
        project_summary = data["projectSummary"]

        return ProjectSummary(
            project_name=project_summary["projectName"],
            description=project_summary["description"],
            duration=project_summary["duration"],
            weekly_effort=project_summary["weeklyEffort"]
        )



    def _create_prerequisites(self, data: dict) -> list[Prerequisite]:
        prerequisites = []
        for pr in data.get("prerequisites", []):
            concepts_raw = pr.get("concepts", [])
            if isinstance(concepts_raw, str):
                concepts_list = [c.strip() for c in concepts_raw.split(",") if c.strip()]
            else:
                concepts_list = concepts_raw
                
            prerequisites.append(
                Prerequisite(
                    topic=pr.get("topic", ""),
                    concepts=concepts_list
                )
            )
        return prerequisites



    def _create_phases(self, data: dict) -> list[RoadmapPhase]:
        phases = []

        for phase in data["phaseWiseLearningPlan"]:
            phases.append(self._create_phase(phase))
        return phases

    def _create_phase(self, data: dict) -> RoadmapPhase:
        def get_list(key):
            val = data.get(key, [])
            if isinstance(val, str):
                return [x.strip() for x in val.split(",") if x.strip()]
            return val
            
        return RoadmapPhase(
            phase=data.get("phase", ""),
            timeline=data.get("timeline", ""),
            weekly_allocation=data.get("weeklyAllocation", ""),
            objectives=get_list("objectives"),
            topics_to_cover=get_list("topicsToCover"),
            action_items=get_list("actionItems")
        )



    def _create_technologies(self, data: dict) -> list[Technology]:
        technologies = []

        for technology in data["technologiesToLearn"]:

            technologies.append(
                Technology(
                    name=technology["name"],
                    purpose=technology["purpose"]
                )
            )
        return technologies


    def _create_resources(self, data: dict) -> list[Resource]:
        resources = []

        for resource in data["learningResources"]:

            resources.append(
                Resource(
                    resource_name=resource["resourceName"],
                    url=resource["url"],
                    type=resource["type"]
                )
            )
        return resources



    def _create_mini_projects(self, data: dict) -> list[MiniProject]:
        mini_projects = []

        for mini_project in data["miniProjects"]:

            mini_projects.append(
                MiniProject(
                    name=mini_project["name"],
                    description=mini_project["description"],
                    estimated_hours=mini_project["estimatedHours"]
                )
            )
        return mini_projects



    def _create_milestones(self, data: dict) -> list[Milestone]:
        milestones = []

        for milestone in data["milestones"]:

            milestones.append(
                Milestone(
                    milestone_id=milestone["milestoneId"],
                    title=milestone["title"],
                    target_week=milestone["targetWeek"],
                    description=milestone["description"]
                )
            )
        return milestones



    def _create_courses(self, data: dict) -> list[Course]:
        courses = []

        for course in data["recommendedCourses"]:

            courses.append(
                Course(
                    platform=course["platform"],
                    course_name=course["courseName"],
                    price=course["price"],
                    link=course["link"]
                )
            )
        return courses



    def _create_certifications(self, data: dict) -> list[Certification]:
        certifications = []

        for certification in data["recommendedCertifications"]:

            certifications.append(
                Certification(
                    name=certification["name"],
                    issuer=certification["issuer"],
                    benefits=certification["benefits"]
                )
            )
        return certifications



    def _create_future_enhancements(self, data: dict) -> list[FutureEnhancement]:
        future_enhancements = []

        for enhancement in data["futureEnhancements"]:

            future_enhancements.append(
                FutureEnhancement(
                    feature=enhancement["feature"],
                    details=enhancement["details"]
                )
            )
        return future_enhancements



    def _create_final_outcome(self, data: dict) -> str:
        return data["finalExpectedOutcome"]