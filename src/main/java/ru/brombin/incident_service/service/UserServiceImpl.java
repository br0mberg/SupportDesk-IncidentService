package ru.brombin.incident_service.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.brombin.incident_service.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            // TODO: получение id из токена (записывать id пользователя как поле в KK (не sub))
            return 1L;
        }
        throw new IllegalStateException("No JWT token found in security context");
    }

    public UserDto findById(Long userId) {
        // TODO: Получение пользователя из микросервиса по id из токена
        // проверка Optional и выброс исключения
        return null; // Заглушка с фиктивными данными
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(role -> role.startsWith("ROLE_"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Роли для текущего пользователя не найдены"));
        }

        throw new IllegalStateException("Аутентификация в контексте безопасности не найдена");
    }
}
