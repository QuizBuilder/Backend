from fastapi import FastAPI
from app.schemas import GenerateQuizRequest, GenerateQuizResponse, AbilityRequest, AbilityResponse, AnswerItem
from app.prompt import build_prompt
from app.gemini_client import call_gemini
from app.parser import extract_json_from_gemini
from app.parser import validate_ai_output
from app.parser import normalize_quiz_response
from app.ML.Filter.filter_questions import filter_questions_with_fallback
from app.ML.IRT.compute_ability import compute_theta
from app.ML.IRT.rating import theta_to_rating
from typing import List, Optional



app = FastAPI(title="AI Quiz Service")


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/generate-quiz", response_model=GenerateQuizResponse)
async def generate_quiz(request: GenerateQuizRequest):
   
    prompt = build_prompt(
        topic=request.topic,
        difficulty=request.difficulty,
        number_of_questions=2*request.noOfQuestions,
        additional_instructions=request.additionalInstruction
    )

    gemini_response = await call_gemini(prompt)

    parsed_json = extract_json_from_gemini(gemini_response)

    validated_response = validate_ai_output(parsed_json)

    normalized_response = normalize_quiz_response(validated_response)

    filtered_questions = filter_questions_with_fallback(normalized_response, request.difficulty, request.noOfQuestions)

    return filtered_questions


@app.post("/compute-rating")
def compute_rating(req: List[AnswerItem]):
    
    answers = [a.model_dump() for a in req]
    
    theta = compute_theta(answers)

    rating, level = theta_to_rating(theta)

    correct = sum(1 for a in answers if a["correct"])
    total = len(answers)
    accuracy = correct / total if total > 0 else 0

    return AbilityResponse(
        theta=round(theta, 3),
        rating=rating,
        level=level,
        correct_answers=correct,
        total_questions=total,
        accuracy=round(accuracy, 2)
    )
