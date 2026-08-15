package com.eventhub.event.config;

import com.eventhub.event.security.ApiKeyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the Event Management Service.
 *
 * <p>Security model:
 * <ul>
 *   <li>Swagger / OpenAPI docs — fully public.</li>
 *   <li>GET /events and GET /events/{id} — public (event browsing).</li>
 *   <li>All other endpoints — require a valid {@code X-API-KEY} header
 *       (enforced by {@link ApiKeyFilter}).</li>
 * </ul>
 *
 * <p>No JWT is processed here; JWT validation lives in the API Gateway.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiKeyFilter apiKeyFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger / OpenAPI — always public
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // Public event browsing (GET only)
                .requestMatchers(HttpMethod.GET, "/events", "/events/{id}").permitAll()
                // Everything else is protected by ApiKeyFilter
                .anyRequest().permitAll()
            )
            // ApiKeyFilter runs before the default username/password filter
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
