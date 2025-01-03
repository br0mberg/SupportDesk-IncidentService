package ru.brombin.incident_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.brombin.incident_service.entity.Incident;

import java.util.List;

public record IncidentWithDetailsDto(
        @NotNull(message = "Incident cannot be null")
        Incident incident,

        @Valid
        List<@Valid ImageDto> imageDtos,

        @Valid UserDto analystDto,

        @Valid UserDto initiatorDto
) {}
