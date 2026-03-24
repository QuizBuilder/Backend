from app.ML.IRT.ability_update import update_theta

difficulty_map = {
    "EASY": -1,
    "MEDIUM": 0,
    "HARD": 1
}

def compute_theta(answers):

    theta = 0

    for ans in answers:

        b = difficulty_map[ans["difficulty"]]

        response = 1 if ans["correct"] else 0

        theta = update_theta(theta, b, response)

    return theta