package ua.lwjerri.lungge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Lungge API")
            .version("1.0.0")
            .contact(new Contact()))
        .components(new Components()
            .addSecuritySchemes("keycloak",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Enter your JWT token in the format: Bearer {token}")));
  }
}