package com.eventhub.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String apiKeyHeaderName = "X-API-KEY";
        final String bearerAuthName = "BearerAuth";
        final String apiKeySchemeName = "ApiKeyAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("EventHub - User & Authentication Service API")
                        .version("1.0.0")
                        .description("Microservice responsible for user registration, authentication, JWT issuance, and user CRUD operations.")
                        .contact(new Contact()
                                .name("EventHub Member 1 Lead")
                                .email("tharushi@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList(apiKeySchemeName).addList(bearerAuthName))
                .components(new Components()
                        .addSecuritySchemes(apiKeySchemeName, new SecurityScheme()
                                .name(apiKeyHeaderName)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("Internal microservice API key required for communication"))
                        .addSecuritySchemes(bearerAuthName, new SecurityScheme()
                                .name(bearerAuthName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT Bearer token obtained from /auth/login")));
    }
}
