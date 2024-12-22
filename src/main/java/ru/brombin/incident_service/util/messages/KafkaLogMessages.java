package ru.brombin.incident_service.util.messages;

public enum KafkaLogMessages {
    DELETE_IMAGE_REQUEST_FAILED("Failed to send delete image request: %s"),
    DELETE_IMAGE_REQUEST_SUCCESS("Delete image request sent successfully with offset: %d");

    private final String message;

    KafkaLogMessages(String message) {
        this.message = message;
    }

    public String getFormatted(Object... args) {
        return String.format(message, args);
    }
}
