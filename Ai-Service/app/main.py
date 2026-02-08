from fastapi import FastAPI
from app.schemas import GenerateQuizRequest, GenerateQuizResponse
from app.prompt import build_prompt
from app.gemini_client import call_gemini
from app.parser import extract_json_from_gemini

app = FastAPI(title="AI Quiz Service")


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/generate-quiz", response_model=GenerateQuizResponse)
async def generate_quiz(request: GenerateQuizRequest):

    prompt = build_prompt(
        topic=request.topic,
        difficulty=request.difficulty,
        number_of_questions=request.number_of_questions,
        additional_instructions=request.additional_instructions
    )

    gemini_response = await call_gemini(prompt)

    parsed_json = extract_json_from_gemini(gemini_response)

    return parsed_json
