from fastapi import FastAPI
from app.schemas import GenerateQuizRequest, GenerateQuizResponse
from app.prompt import build_prompt
from app.gemini_client import call_gemini
from app.parser import extract_json_from_gemini
from app.parser import validate_ai_output
from app.parser import normalize_quiz_response

app = FastAPI(title="AI Quiz Service")


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/generate-quiz", response_model=GenerateQuizResponse)
async def generate_quiz(request: GenerateQuizRequest):
    print(request)
    prompt = build_prompt(
        topic=request.topic,
        difficulty=request.difficulty,
        number_of_questions=request.noOfQuestions,
        additional_instructions=request.additionalInstruction
    )

    gemini_response = await call_gemini(prompt)

    parsed_json = extract_json_from_gemini(gemini_response)

    validated_response = validate_ai_output(parsed_json)

    normalized_response = normalize_quiz_response(validated_response)

    return normalized_response
