import os
import httpx
from fastapi import HTTPException

GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"


async def call_gemini(prompt: str) -> dict:
    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        raise RuntimeError("GEMINI_API_KEY not set")

    url = f"{GEMINI_API_URL}?key={api_key}"

    payload = {
        "contents": [
            {
                "parts": [
                    {"text": prompt}
                ]
            }
        ]
    }

    try:
        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.post(
                url,
                json=payload,
                headers={"Content-Type": "application/json"}
            )

        if response.status_code != 200:
            raise HTTPException(
                status_code=502,
                detail="External AI service returned an error"
            )

        return response.json()

    except httpx.TimeoutException:
        raise HTTPException(
            status_code=504,
            detail="AI service timed out"
        )

    except httpx.RequestError:
        raise HTTPException(
            status_code=503,
            detail="AI service unavailable"
        )

    except HTTPException:
        # re-raise FastAPI HTTP exceptions as-is
        raise

    except Exception:
        raise HTTPException(
            status_code=500,
            detail="Unexpected server error"
        )
