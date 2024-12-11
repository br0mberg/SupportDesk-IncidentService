package ru.brombin.incident_service.service;

import ru.brombin.incident_service.dto.UserDto;

import java.util.Optional;

public interface UserService {
    Long getCurrentUserId();
    UserDto findById(Long userId);
    String getCurrentUserRole();
}
