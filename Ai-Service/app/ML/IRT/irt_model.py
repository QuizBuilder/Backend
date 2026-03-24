import numpy as np

def sigmoid(z):
    return 1 / (1 + np.exp(-z))

def probability(theta, b):
    return sigmoid(theta - b)