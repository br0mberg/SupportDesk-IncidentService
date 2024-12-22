package ru.brombin.incident_service.util.messages;

public enum UserLogMessages {
    USER_NOT_FOUND("User with ID '%s' not found"),
    CURRENT_USER_ID_FETCHED("Current user ID fetched: %d"),
    CURRENT_USER_ROLE_FETCHED("Current user role fetched: '%s'");

    private final String message;

    UserLogMessages(String message) {
        this.message = message;
    }

    public String getFormatted(Object... args) {
        return String.format(message, args);
    }
}

