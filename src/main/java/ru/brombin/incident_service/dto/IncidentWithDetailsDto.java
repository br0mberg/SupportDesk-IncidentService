package ru.brombin.incident_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record IncidentWithDetailsDto(
        @NotNull(message = "Incident cannot be null")
        IncidentDto incidentDto,

        @Valid
        List<@Valid ImageDto> imageDtos,

        @Valid UserDto analystDto,

        @Valid UserDto initiatorDto
) {}
