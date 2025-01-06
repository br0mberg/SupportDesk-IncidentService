package ru.brombin.incident_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import ru.brombin.incident_service.dto.UserDto;
import ru.brombin.incident_service.service.grpc.GrpcClientService;
import user.UserServiceOuterClass.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    @Mock
    SecurityContextHolder securityContextHolder;

    @Mock
    GrpcClientService grpcClientService;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        securityContextHolder.setContext(securityContext);
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldReturnUserId_whenJwtTokenIsPresent() {
        // Arrange
        String userIdFieldName = "user_id";
        Long expectedUserId = 123L;

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(userIdFieldName)).thenReturn(String.valueOf(expectedUserId));

        JwtAuthenticationToken jwtAuthenticationToken = mock(JwtAuthenticationToken.class);
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);

        Authentication authentication = jwtAuthenticationToken;
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Long actualUserId = userService.getCurrentUserId();

        // Assert
        assertThat(actualUserId).isEqualTo(expectedUserId);
        verify(jwt, times(1)).getClaimAsString(userIdFieldName);
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
    void getCurrentUserRole_shouldReturnRole_whenAuthenticationIsValid() {
        // Arrange
        String expectedRole = "ROLE_ADMIN";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                .thenReturn(Collections.singletonList(expectedRole).stream());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String actualRole = userService.getCurrentUserRole();

        // Assert
        assertThat(actualRole).isEqualTo(expectedRole);
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
