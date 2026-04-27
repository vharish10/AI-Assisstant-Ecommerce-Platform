package com.Revature.Ecommerce.Platform.Config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {

    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI().info(new Info().title("E-Commerce Product API")
                .description("Product Catalog APIs with search, filters, pagination"));
    }
}