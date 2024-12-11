package ru.brombin.incident_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto (
        @NotBlank(message = "Role must not be empty")
        String role,

        @NotBlank(message = "Full name must not be empty")
        String fullName,

        @NotBlank(message = "Login must not be empty")
        String login,

        @NotBlank(message = "Email must not be empty")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Phone number must not be empty")
        @Size(min = 5, max = 15, message = "Phone number should be between 5 and 15 characters")
        String phoneNumber,

        @NotBlank(message = "Workplace location must not be empty")
        String workplaceLocation
){}
