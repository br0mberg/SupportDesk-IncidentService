package ru.brombin.incident_service.util.exceptions;

public class UserProcessingException extends RuntimeException {
    public UserProcessingException(String message) {
        super(message);
    }
}
