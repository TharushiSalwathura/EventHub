package com.eventhub.payment.config;

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
        final String apiKeySchemeName = "ApiKeyAuth";
        final String bearerSchemeName = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("EventHub - Payment & Notification Microservice API")
                        .description("RESTful microservice for payment transaction processing, receipt generation, and system notifications/audit logs.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EventHub Team - Member 4 & 5")
                                .email("payment-notif@eventhub.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(apiKeySchemeName)
                        .addList(bearerSchemeName))
                .components(new Components()
                        .addSecuritySchemes(apiKeySchemeName,
                                new SecurityScheme()
                                        .name("X-API-KEY")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Internal API Key header for service-to-service communication"))
                        .addSecuritySchemes(bearerSchemeName,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Token forwarded by API Gateway")));
    }
}
