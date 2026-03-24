import json
import numpy as np
import joblib

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression

from app.ML.Preprocessing.text_cleaner import clean_text
from app.ML.Models.logistic_regression import LogisticRegressionScratch




with open("app/quiz_dataset.json") as f:
    data = json.load(f)


texts = []
labels = []

for q in data[0]['questions']:
    correct_option = q["options"][q["correct_index"]]

    full_text = (
        q["question"] + " " +
        " ".join(q["options"]) + " " +
        correct_option
    )
    texts.append(clean_text(full_text))
    labels.append(q["difficulty"])




label_map = {
    "EASY": 0,
    "MEDIUM": 1,
    "HARD": 2
}

reverse_map = {v: k for k, v in label_map.items()}

y = np.array([label_map[l] for l in labels])



vectorizer = TfidfVectorizer()

X = vectorizer.fit_transform(texts).toarray()




X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42
)




model_easy = LogisticRegressionScratch()
model_medium = LogisticRegressionScratch()
model_hard = LogisticRegressionScratch()

y_easy = (y_train == 0).astype(int)
y_medium = (y_train == 1).astype(int)
y_hard = (y_train == 2).astype(int)

model_easy.fit(X_train, y_easy)
model_medium.fit(X_train, y_medium)
model_hard.fit(X_train, y_hard)


def predict_ovr(x):

    p_easy = model_easy.predict_proba(x)
    p_medium = model_medium.predict_proba(x)
    p_hard = model_hard.predict_proba(x)

    probs = np.array([p_easy, p_medium, p_hard]).flatten()

    return np.argmax(probs)



correct = 0

for i in range(len(X_test)):

    x = X_test[i].reshape(1, -1)

    pred = predict_ovr(x)

    if pred == y_test[i]:
        correct += 1

scratch_accuracy = correct / len(X_test)

print("Scratch OvR Accuracy:", scratch_accuracy)




sk_model = LogisticRegression(max_iter=1000)

sk_model.fit(X_train, y_train)

sk_preds = sk_model.predict(X_test)

sk_accuracy = np.mean(sk_preds == y_test)

print("Sklearn Accuracy:", sk_accuracy)



if sk_accuracy >= scratch_accuracy:
    print("Using Sklearn Model")

    joblib.dump(sk_model, "app/ML/Models/final_model.pkl")
    joblib.dump(vectorizer, "app/ML/Models/tfidf_vectorizer.pkl")

    model_type = "sklearn"

else:
    print("Using Scratch OvR Model")

    joblib.dump(model_easy, "app/ML/Models/model_easy.pkl")
    joblib.dump(model_medium, "app/ML/Models/model_medium.pkl")
    joblib.dump(model_hard, "app/ML/Models/model_hard.pkl")
    joblib.dump(vectorizer, "app/ML/Models/tfidf_vectorizer.pkl")

    model_type = "scratch"



joblib.dump(model_type, "app/ML/Models/model_type.pkl")