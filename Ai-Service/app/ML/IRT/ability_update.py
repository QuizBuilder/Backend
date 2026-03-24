from app.ML.IRT.irt_model import probability

def update_theta(theta, b, response, lr=0.1):

    pred = probability(theta, b)

    theta = theta + lr * (response - pred)

    return theta