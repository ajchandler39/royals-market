package com.royalsmarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI royalsMarketOpenApi() {
        return new OpenAPI().info(new Info()
                .title("RoyalsMarket API")
                .version("v1")
                .description("JSON API for the RoyalsMarket MapleStory marketplace. "
                        + "Reads are public; writes require HTTP Basic authentication.")
                .license(new License().name("MIT")));
    }
}
