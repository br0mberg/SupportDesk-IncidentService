package ru.brombin.incident_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.authorizeHttpRequests(c -> c
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // 1. Получаем realm_access из payload JWT
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            // 2. Извлекаем список ролей
            List<String> roles = (realmAccess != null && realmAccess.containsKey("roles"))
                    ? (List<String>) realmAccess.get("roles")
                    : Collections.emptyList();

            // 3. Преобразуем роли в GrantedAuthority
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Добавляем префикс ROLE_
                    .collect(Collectors.toList());
        });

        // Устанавливаем поле для имени пользователя
        converter.setPrincipalClaimName("preferred_username");

        return converter;
    }
}
