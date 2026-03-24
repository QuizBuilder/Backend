import requests
import json
import os
import time

url = "http://127.0.0.1:8000/generate-quiz"

topics = [
    "OPERATING_SYSTEMS",
    "DBMS",
    "COMPUTER_NETWORKS",
    "DATA_STRUCTURES",
    "ALGORITHMS",
    "SOFTWARE_ENGINEERING",
    "OBJECT_ORIENTED_PROGRAMMING"
]

dataset = []
req_count = 0
max_requests = 10
i = 10

while req_count < max_requests:

    topic = topics[i % len(topics)]

    payload = {
        "topic": topic,
        "difficulty": "HARD",
        "noOfQuestions": 25
    }

    print(f"Request {req_count+1} for {topic}")

    response = requests.post(url, json=payload, timeout=100)

    if response.status_code == 200:
        data = response.json()

        if isinstance(data, list):
            dataset.extend(data)
        else:
            dataset.append(data)

        req_count += 1
        print(f"Total questions: {len(dataset)}")

    else:
        print("Status:", response.status_code)
        print("Response:", response.text) 
        print("Request failed ❌")

    i += 1
    time.sleep(1)



file_path = "app/quiz_dataset.json"


if os.path.exists(file_path):
    with open(file_path, "r") as f:
        existing_data = json.load(f)
else:
    existing_data = []


existing_data.extend(dataset)


with open(file_path, "w") as f:
    json.dump(existing_data, f, indent=2)

print(f"Total questions now: {len(existing_data)}")

print("500 Questions Dataset Created 🚀")