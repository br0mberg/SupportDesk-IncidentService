package ru.brombin.incident_service.util.messages;

public enum SecurityLogMessages {
    NO_JWT_TOKEN_FOUND("No JWT token found in security context"),
    USER_ROLE_NOT_FOUND("Roles for the current user not found"),
    AUTHENTICATION_NOT_FOUND("Authentication in security context not found");

    private final String message;

    SecurityLogMessages(String message) {
        this.message = message;
    }

    public String getFormatted(Object... args) {
        return String.format(message, args);
    }
}

