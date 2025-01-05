package ru.brombin.incident_service.dto;

import java.time.LocalDateTime;

public record IncidentFilterDto (
        String status,
        String category,
        String priority,
        String responsibleService,
        LocalDateTime fromDate,
        LocalDateTime toDate
) {}
