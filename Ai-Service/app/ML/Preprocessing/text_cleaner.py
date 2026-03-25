import re
import nltk
nltk.download('stopwords')
from nltk.corpus import stopwords


stop_words = set(stopwords.words("english"))

def clean_text(text):

    text = text.lower()

    text = re.sub(r"[^\w\s]", "", text)

    text = text.strip()

    text = text.split()

    words = []
    for word in text:
        if word not in stop_words:
            words.append(word)


    text = " ".join(words)

    return text
text = "What is the difference between Stack and Queue?"


    