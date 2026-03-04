import json
from fastapi import HTTPException
from pydantic import ValidationError
from app.schemas import GenerateQuizResponse


def extract_json_from_gemini(response: dict) -> dict:
    try:
        # Extract raw text from Gemini response
        text = response["candidates"][0]["content"]["parts"][0]["text"]

        # Remove markdown code fences if present
        cleaned = text.replace("```json", "").replace("```", "").strip()

        # Extract JSON block only (defensive parsing)
        start = cleaned.find("{")
        end = cleaned.rfind("}")

        if start == -1 or end == -1 or start >= end:
            raise ValueError("No valid JSON object found in AI response")

        json_text = cleaned[start:end + 1]

        return json.loads(json_text)

    except (KeyError, IndexError):
        raise HTTPException(
            status_code=502,
            detail={
                "error": "AI_RESPONSE_STRUCTURE_ERROR",
                "message": "Invalid AI response structure"
            }
        )

    except json.JSONDecodeError:
        raise HTTPException(
            status_code=422,
            detail={
                "error": "INVALID_AI_JSON",
                "message": "AI returned malformed JSON"
            }
        )

    except Exception:
        raise HTTPException(
            status_code=422,
            detail={
                "error": "AI_JSON_EXTRACTION_FAILED",
                "message": "Failed to extract valid JSON from AI response"
            }
        )


def validate_ai_output(data: dict) -> GenerateQuizResponse:
    try:
        print(data)
        return GenerateQuizResponse(**data)
    except ValidationError as e:
        raise HTTPException(
            status_code=422,
            detail={
                "error": "INVALID_AI_OUTPUT",
                "message": "AI response does not match expected quiz schema",
                "details": e.errors()
            }
        )


def normalize_quiz_response(response: GenerateQuizResponse) -> GenerateQuizResponse:
    normalized_questions = []

    for q in response.questions:
        question_text = q.question.strip()
        if not question_text:
            raise HTTPException(
                status_code=422,
                detail={
                    "error": "EMPTY_QUESTION_TEXT",
                    "message": "Question text is empty after normalization"
                }
            )

        cleaned_options = [opt.strip() for opt in q.options if opt.strip()]

        seen = set()
        unique_options = []
        for opt in cleaned_options:
            key = opt.lower()
            if key not in seen:
                seen.add(key)
                unique_options.append(opt)

        if len(unique_options) != 4:
            raise HTTPException(
                status_code=422,
                detail={
                    "error": "INVALID_OPTIONS",
                    "message": "Options must contain exactly 4 unique values"
                }
            )

        normalized_questions.append({
            "question": question_text,
            "options": unique_options,
            "correct_index": q.correct_index
        })

    return GenerateQuizResponse(questions=normalized_questions)
