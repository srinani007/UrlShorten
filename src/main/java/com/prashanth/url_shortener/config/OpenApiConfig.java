package com.prashanth.url_shortener.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "🔥 URL Shortener API",
        version = "1.0.0",
        description = "Built by Venkat Sai Prasanth 🧠 | Spring Boot + PostgreSQL + Redis + Kafka"
    ),
    servers = @Server(url = "http://localhost:8080")
)
public class OpenApiConfig {
}
