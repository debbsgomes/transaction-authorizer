package com.example.transaction_authorizer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI transactionAuthorizerAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transaction Authorizer API")
                        .description("API for authorizing transactions with multiple benefit categories")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Your Team Name")
                                .email("team@example.com")));
    }
}
