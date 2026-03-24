import joblib
import numpy as np

from app.ML.Preprocessing.text_cleaner import clean_text


model_type = joblib.load("app/ML/Models/model_type.pkl")

vectorizer = joblib.load("app/ML/Models/tfidf_vectorizer.pkl")

label_map = {
    0: "EASY",
    1: "MEDIUM",
    2: "HARD"
}


if model_type == "sklearn":

    model = joblib.load("app/ML/Models/final_model.pkl")

else:

    model_easy = joblib.load("app/ML/Models/model_easy.pkl")
    model_medium = joblib.load("app/ML/Models/model_medium.pkl")
    model_hard = joblib.load("app/ML/Models/model_hard.pkl")


def predict_difficulty(question: str):

 
    question = clean_text(question)

    vec = vectorizer.transform([question]).toarray()


   
    if model_type == "sklearn":

        pred = model.predict(vec)[0]

        return label_map[pred]


   
    else:

        p_easy = model_easy.predict_proba(vec)
        p_medium = model_medium.predict_proba(vec)
        p_hard = model_hard.predict_proba(vec)

        probs = np.array([p_easy, p_medium, p_hard]).flatten()

        pred = np.argmax(probs)

        return label_map[pred]