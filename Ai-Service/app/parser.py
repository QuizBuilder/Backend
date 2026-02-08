import json
from fastapi import HTTPException


def extract_json_from_gemini(response: dict) -> dict:
    try:
        text = response["candidates"][0]["content"]["parts"][0]["text"]

        # Remove markdown code fences if present
        text = text.strip()
        if text.startswith("```"):
            text = text.replace("```json", "").replace("```", "").strip()

        return json.loads(text)

    except (KeyError, IndexError):
        raise HTTPException(
            status_code=502,
            detail="Invalid AI response structure"
        )

    except json.JSONDecodeError:
        raise HTTPException(
            status_code=502,
            detail="AI returned invalid JSON"
        )
