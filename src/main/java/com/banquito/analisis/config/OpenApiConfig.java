package com.banquito.analisis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Motor de Análisis Crediticio Automotriz")
                        .description("API para evaluación automática de solicitudes de crédito automotriz")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Banquito")
                                .email("soporte@banquito.com")
                                .url("https://www.banquito.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de Desarrollo"),
                        new Server().url("https://api.banquito.com").description("Servidor de Producción")
                ));
    }
} 