package ru.lanit.minobr.service.quick_start.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "SWAGGER_NAME", version = "SWAGGER_VERSION"))
public class SpringdocConfig {
}
