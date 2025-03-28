package com.rest.challenge.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Rest Challenge API",
        version = "1.0",
        description = "REST API for managing users in Spring Boot"
    )
)
public class SwaggerConfig {
}
