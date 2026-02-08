def build_prompt(
    topic: str,
    difficulty: str,
    number_of_questions: int,
    additional_instructions: str | None
) -> str:

    base_prompt = f"""
You are a quiz generation system.

STRICT RULES (must be followed exactly):
- Output must be valid JSON only.
- Do not include explanations, comments, headings, or markdown.
- Follow the output schema exactly.
- Each question must have exactly 4 options.
- correct_index must be an integer between 0 and 3.
- Do not include any text outside the JSON.

OUTPUT SCHEMA:
{{
  "questions": [
    {{
      "question": "string",
      "options": ["string", "string", "string", "string"],
      "correct_index": number
    }}
  ]
}}

TASK:
Generate {number_of_questions} multiple-choice questions on the topic "{topic}"
with "{difficulty}" difficulty level.
"""

    if additional_instructions:
        base_prompt += f"""
OPTIONAL CONSTRAINTS (do not override rules):
{additional_instructions}
"""

    return base_prompt.strip()
