package ru.brombin.incident_service.dto;

import ru.brombin.incident_service.entity.IncidentCategory;
import ru.brombin.incident_service.entity.IncidentPriority;
import ru.brombin.incident_service.entity.IncidentStatus;
import ru.brombin.incident_service.entity.ResponsibleService;

import java.time.LocalDateTime;

public record IncidentFilterDto (
        IncidentStatus status,
        IncidentCategory category,
        IncidentPriority priority,
        ResponsibleService responsibleService,
        LocalDateTime fromDate,
        LocalDateTime toDate
) {}
