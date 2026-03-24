from typing import List
from app.ML.Inference.predict_difficulty import predict_difficulty
from app.schemas import GenerateQuizResponse, QuestionResponse


def filter_questions_with_fallback(
    response: GenerateQuizResponse,
    requested_difficulty: str,
    required_count: int
) -> GenerateQuizResponse:

    primary = []
    fallback = []
    others = []

    fallback_rules = {
        "HARD": ("MEDIUM", 0.3),
        "MEDIUM": ("EASY", 0.3),
        "EASY": ("MEDIUM", 0.1)
    }

    fallback_diff, fallback_ratio = fallback_rules[requested_difficulty]
    max_fallback = int(required_count * fallback_ratio)

   

    for q in response.questions:

        predicted = predict_difficulty(q.question)

        if predicted == requested_difficulty:
            primary.append(q)

        elif predicted == fallback_diff:
            fallback.append(q)

        else:
            others.append(q)

    final_questions: List[QuestionResponse] = []

    

    for q in primary:
        final_questions.append(q)
        if len(final_questions) == required_count:
            return GenerateQuizResponse(questions=final_questions)

   

    fallback_used = 0

    for q in fallback:

        if fallback_used >= max_fallback:
            break

        final_questions.append(q)
        fallback_used += 1

        if len(final_questions) == required_count:
            return GenerateQuizResponse(questions=final_questions)

    

    for q in others:

        final_questions.append(q)

        if len(final_questions) == required_count:
            return GenerateQuizResponse(questions=final_questions)

   
    return GenerateQuizResponse(questions=final_questions[:required_count])