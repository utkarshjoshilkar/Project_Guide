import json


class SchemaGenerator:

    def generate_schema(self) -> str:
        return json.dumps(self._build_schema(), indent=2)

    def _build_schema(self) -> dict:
        return {

            "projectSummary": {
                "projectName": "",
                "description": "",
                "duration": "",
                "weeklyEffort": ""
            },

            "prerequisites": [
                {
                    "topic": "",
                    "concepts": ""
                }
            ],

            "phaseWiseLearningPlan": [
                {
                    "phase": "",
                    "timeline": "",
                    "weeklyAllocation": "",
                    "objectives": [],
                    "topicsToCover": [],
                    "actionItems": []
                }
            ],

            "technologiesToLearn": [
                {
                    "name": "",
                    "purpose": ""
                }
            ],

            "learningResources": [
                {
                    "resourceName": "",
                    "url": "",
                    "type": ""
                }
            ],

            "miniProjects": [
                {
                    "name": "",
                    "description": "",
                    "estimatedHours": 0
                }
            ],

            "milestones": [
                {
                    "milestoneId": 0,
                    "title": "",
                    "targetWeek": "",
                    "description": ""
                }
            ],

            "recommendedCourses": [
                {
                    "platform": "",
                    "courseName": "",
                    "price": "",
                    "link": ""
                }
            ],

            "recommendedCertifications": [
                {
                    "name": "",
                    "issuer": "",
                    "benefits": ""
                }
            ],

            "futureEnhancements": [
                {
                    "feature": "",
                    "details": ""
                }
            ],

            "finalExpectedOutcome": ""

        }