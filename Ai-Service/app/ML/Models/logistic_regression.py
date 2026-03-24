import numpy as np

class LogisticRegressionScratch:

    def __init__(self, lr=0.1, epochs=1000):
        self.lr = lr
        self.epochs = epochs

    def sigmoid(self, z):
        return 1 / (1 + np.exp(-z))

    def fit(self, X, y):

        n, m = X.shape

        self.w = np.zeros(m)
        self.b = 0

        for _ in range(self.epochs):

            z = np.dot(X, self.w) + self.b
            y_pred = self.sigmoid(z)

            dw = (1/n) * np.dot(X.T, (y_pred - y))
            db = (1/n) * np.sum(y_pred - y)

            self.w -= self.lr * dw
            self.b -= self.lr * db

    def predict_proba(self, X):
        z = np.dot(X, self.w) + self.b
        return self.sigmoid(z)

    def predict(self, X):
        probs = self.predict_proba(X)
        return np.array([1 if p > 0.5 else 0 for p in probs])