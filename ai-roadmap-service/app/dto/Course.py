from pydantic import BaseModel


class Course(BaseModel):

    platform: str
    course_name: str
    price: str
    link: str