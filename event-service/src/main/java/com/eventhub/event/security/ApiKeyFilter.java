package com.eventhub.event.security;

import com.eventhub.event.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Servlet filter that validates the {@code X-API-KEY} header on every request.
 *
 * <p>Bypass rules:
 * <ul>
 *   <li>Swagger / OpenAPI paths are always allowed.</li>
 *   <li>GET requests to {@code /events} and {@code /events/{id}} are allowed without
 *       an API key (public event browsing as per spec).</li>
 *   <li>All other requests require a valid {@code X-API-KEY} header.</li>
 * </ul>
 */
@Slf4j
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Value("${api.key}")
    private String expectedApiKey;

    private final ObjectMapper objectMapper;

    public ApiKeyFilter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // ─── Bypass Rules ────────────────────────────────────────────────────────

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path   = request.getRequestURI();
        String method = request.getMethod();

        // Always allow Swagger / API-docs paths
        if (path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.equals("/swagger-ui.html") ||
            path.startsWith("/actuator")) {
            return true;
        }

        // Public read access: GET /events  and  GET /events/{id}
        // but NOT GET /events/search or GET /events/{id}/availability (gateway handles auth)
        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            if (path.equals("/events") ||
                path.matches("/events/\\d+$")) {
                return true;
            }
        }

        return false;
    }

    // ─── Validation Logic ─────────────────────────────────────────────────────

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestApiKey = request.getHeader(API_KEY_HEADER);

        if (requestApiKey == null || !requestApiKey.equals(expectedApiKey)) {
            log.warn("Invalid or missing X-API-KEY for {} {}", request.getMethod(), request.getRequestURI());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message("Invalid or missing X-API-KEY header")
                    .path(request.getRequestURI())
                    .build();

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
