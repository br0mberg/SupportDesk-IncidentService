package ru.brombin.incident_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.brombin.incident_service.entity.IncidentCategory;
import ru.brombin.incident_service.entity.IncidentPriority;
import ru.brombin.incident_service.entity.IncidentStatus;
import ru.brombin.incident_service.entity.ResponsibleService;

import java.time.LocalDateTime;
import java.util.List;

public record IncidentDto(
        @NotBlank
        @Size(max = 255, message = "Name should be between 2 and 255 characters")
        String name,

        @NotBlank(message = "Description should not be empty")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        LocalDateTime dateClosed,

        @Positive(message = "Analyst ID must be positive")
        Long analystId,

        IncidentStatus incidentStatus,

        IncidentPriority incidentPriority,

        IncidentCategory category,

        ResponsibleService responsibleService
) {}