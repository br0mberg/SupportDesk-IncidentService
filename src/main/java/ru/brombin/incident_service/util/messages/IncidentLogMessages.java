package ru.brombin.incident_service.util.messages;

public enum IncidentLogMessages {

    INCIDENT_FETCH_ALL("Fetching all incidents"),
    INCIDENT_FETCH_PAGINATED("Fetching incidents with pagination: page={}, size={}"),
    INCIDENT_NOT_FOUND("Incident with ID '%s' not found"),
    INCIDENT_UPDATED("Incident with ID: %s updated successfully"),
    INCIDENT_CREATED("Incident with ID: %s created successfully"),
    INCIDENT_DELETED("Incident with ID: %s deleted successfully"),
    INCIDENT_STATUS_UPDATED("Incident status updated for ID '%s', new status='%s'"),
    INCIDENT_ANALYST_UPDATED("Incident analyst updated for ID '%s', new analyst ID='%s'"),
    INCIDENT_PRIORITY_UPDATED("Incident priority updated for ID '%s', new priority='%s'"),
    INCIDENT_RESPONSIBLE_SERVICE_UPDATED("Incident responsible service updated for ID '%s', new service='%s'"),
    INCIDENT_CATEGORY_UPDATED("Incident category updated for ID '%s', new category='%s'");

    private final String message;

    IncidentLogMessages(String message) {
        this.message = message;
    }

    public String getFormatted(Object... args) {
        return String.format(message, args);
    }
}
