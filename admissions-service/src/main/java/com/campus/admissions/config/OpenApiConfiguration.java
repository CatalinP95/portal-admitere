package com.campus.admissions.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Admissions Service API")
                        .description("""
                                Serviciu pentru gestionarea admiterii:
                                - cereri de admitere
                                - sesiuni
                                - statistici
                                - contracte
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Portal Admitere")
                                .email("admin@portal-admitere.ro")));
    }
}