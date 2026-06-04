package com.LGB.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local server");

        Info info = new Info()
                .title("LGB")
                .description("Spring Boot API documentation")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("LGB")
                        .email("admin@example.com"));

        return new OpenAPI()
                .servers(List.of(localServer))
                .info(info);
    }
}