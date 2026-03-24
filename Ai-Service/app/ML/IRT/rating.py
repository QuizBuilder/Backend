def theta_to_rating(theta):

    rating = ((theta + 2) / 4) * 100

    rating = max(0, min(100, rating))

    if rating < 30:
        level = "Beginner"
    elif rating < 60:
        level = "Intermediate"
    elif rating < 80:
        level = "Advanced"
    else:
        level = "Expert"

    return round(rating, 2), level