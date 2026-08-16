package com.eventhub.event.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configures the OpenAPI 3.0 specification document served at /v3/api-docs.
 * Adds the X-API-KEY header security scheme so Swagger UI shows the lock icon.
 */
@Configuration
public class OpenApiConfig {

    private static final String API_KEY_SCHEME = "ApiKeyAuth";

    @Bean
    public OpenAPI eventServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EventHub — Event Management Service API")
                        .description("""
                                REST API for managing events in the EventHub platform.
                                
                                **Authentication:** Include `X-API-KEY: event-service-secret-key-12345` header
                                for write operations (POST, PUT, DELETE) and advanced queries.
                                
                                Public read access (GET /events, GET /events/{id}) does not require an API key.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EventHub Team — Member 2")
                                .email("member2@eventhub.dev"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Direct Service (local)"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(API_KEY_SCHEME,
                                new SecurityScheme()
                                        .name("X-API-KEY")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Internal API key for service-to-service communication")));
    }
}
