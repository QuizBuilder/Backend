package com.quizBuilder.project.Exception;

public abstract class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
