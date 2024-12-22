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
import ru.brombin.incident_service.util.messages.SecurityLogMessages;
import ru.brombin.incident_service.util.messages.UserLogMessages;
import user.UserServiceOuterClass.UserResponse;

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

            log.info(UserLogMessages.CURRENT_USER_ID_FETCHED.getFormatted(userId));
            return userId;
        }

        throw new SecurityException(SecurityLogMessages.NO_JWT_TOKEN_FOUND.getFormatted());
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

        log.info(UserLogMessages.CURRENT_USER_ROLE_FETCHED.getFormatted(userDto.role()));

        return Optional.of(userDto);
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(r -> r.startsWith("ROLE_"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(SecurityLogMessages.USER_ROLE_NOT_FOUND.getFormatted()));

            log.info(UserLogMessages.CURRENT_USER_ROLE_FETCHED.getFormatted(role));
            return role;
        }

        throw new IllegalStateException(SecurityLogMessages.AUTHENTICATION_NOT_FOUND.getFormatted());
    }
}
