package ru.brombin.incident_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import ru.brombin.incident_service.dto.UserDto;
import ru.brombin.incident_service.service.grpc.GrpcClientService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
@FieldDefaults(level= AccessLevel.PRIVATE)
class UserServiceImplTest {
    @Mock
    SecurityContextHolder securityContextHolder;
    @Mock
    GrpcClientService grpcClientService;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        // Очистка контекста после каждого теста
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldThrowSecurityException_whenAuthenticationIsMissing() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(null);

        // Act & Assert
        assertThrows(SecurityException.class, () -> userService.getCurrentUserId());
    }

    @Test
    void findById_shouldReturnUserDto_whenUserExists() {
        // Arrange
        Long userId = 123L;
        UserResponse userResponse = UserResponse.newBuilder()
                .setRole("ADMIN")
                .setFullName("John Doe")
                .setLogin("johndoe")
                .setEmail("john@example.com")
                .setPhoneNumber("123456789")
                .setWorkplaceLocation("Office 42")
                .build();
        when(grpcClientService.findUserById(userId)).thenReturn(userResponse);

        // Act
        Optional<UserDto> result = userService.findById(userId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().role()).isEqualTo("ADMIN");
        assertThat(result.get().fullName()).isEqualTo("John Doe");
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenUserNotFound() {
        // Arrange
        Long userId = 123L;
        when(grpcClientService.findUserById(userId)).thenReturn(null);

        // Act
        Optional<UserDto> result = userService.findById(userId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getCurrentUserRole_shouldThrowIllegalStateException_whenRolesNotFound() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getAuthorities()).thenReturn(List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.getCurrentUserRole());
    }

    @Test
    void getCurrentUserRole_shouldThrowIllegalStateException_whenAuthenticationIsMissing() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(null);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.getCurrentUserRole());
    }
}
*/