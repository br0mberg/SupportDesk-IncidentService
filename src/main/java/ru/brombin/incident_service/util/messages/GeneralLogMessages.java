package ru.brombin.incident_service.util.messages;

public enum GeneralLogMessages {
    NOT_FOUND("Resource not found: %s - Path: %s"),
    VALIDATION_FAILED("Validation failed: %s"),
    IMAGE_PROCESSING_ERROR("Image processing error: %s - Path: %s"),
    UNEXPECTED_ERROR("An unexpected error occurred: %s - Path: %s");

    private final String message;

    GeneralLogMessages(String message) {
        this.message = message;
    }

    public String getFormatted(String... args) {
        return String.format(message, args);
    }
}