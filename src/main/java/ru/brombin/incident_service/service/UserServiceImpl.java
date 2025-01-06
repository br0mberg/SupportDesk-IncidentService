package ru.brombin.incident_service.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.brombin.incident_service.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import ru.brombin.incident_service.service.grpc.GrpcClientService;
import user.UserServiceOuterClass.*;

import java.util.Optional;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level=PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    GrpcClientService grpcClientService;

    @Value("${jwt.token.user-id.field-name}")
    String userIdFieldName;

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            Long userId = Long.parseLong(jwt.getClaimAsString(userIdFieldName));

            log.info("Current user with id: {} fetched from jwt token", userId);
            return userId;
        }

        throw new SecurityException("No JWT token found in security context");
    }

    @Override
    public Optional<UserDto> findById(Long userId) {
       UserResponse userResponse = grpcClientService.findUserById(userId);

        if (userResponse == null) {
            return Optional.empty();
        }

        UserDto userDto = new UserDto(userResponse.getRole(), userResponse.getFullName(),
                userResponse.getLogin(), userResponse.getEmail(),
                userResponse.getPhoneNumber(), userResponse.getWorkplaceLocation());

        log.info("Current User role fetched: {}", userDto.role());

        return Optional.of(userDto);
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(r -> r.startsWith("ROLE_"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Roles for the current user not found"));

            log.info("Current User role fetched: {}", role);
            return role;
        }

        throw new IllegalStateException("Authentication in security context not found");
    }
}
