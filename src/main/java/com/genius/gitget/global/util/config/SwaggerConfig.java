package com.genius.gitget.global.util.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todoffin API")
                        .description("TeamTheGenius의 todoffin API 문서입니다.")
                        .version("1.0.0"));
    }
}
