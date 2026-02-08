from typing import List, Optional
from pydantic import BaseModel, Field, conint, constr


class GenerateQuizRequest(BaseModel):
    topic: constr(min_length=1)
    difficulty: constr(min_length=1)
    number_of_questions: conint(gt=0)
    additional_instructions: Optional[constr(max_length=200)] = None


class QuestionResponse(BaseModel):
    question: str
    options: List[str] = Field(..., min_items=4, max_items=4)
    correct_index: conint(ge=0, le=3)


class GenerateQuizResponse(BaseModel):
    questions: List[QuestionResponse]
