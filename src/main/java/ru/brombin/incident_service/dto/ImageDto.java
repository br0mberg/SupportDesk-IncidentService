package ru.brombin.incident_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ImageDto (
        @NotNull(message = "Incident ID must not be null")
        Long incidentId,

        @NotBlank(message = "File name must not be empty")
        String fileName,

        @Positive(message = "Size must be a positive number")
        long size,

        @NotBlank(message = "Type must not be empty")
        String type,

        byte[] content
){}
