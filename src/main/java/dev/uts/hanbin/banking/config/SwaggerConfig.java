package dev.uts.hanbin.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
public class SwaggerConfig {

  @Bean
  public OpenAPI openApi() {
    Info info =
        new Info().title("Banking Service App").description("Banking Service App API Documentation")
            .version("v1");

    return new OpenAPI().info(info);
  }

  @Primary
  @Bean
  public SwaggerUiConfigProperties swaggerUiConfig() {
    SwaggerUiConfigProperties config = new SwaggerUiConfigProperties();
    config.setDefaultModelsExpandDepth(-1); // Disable model expansion by default
    config.setTagsSorter("alpha"); // Sort tags alphabetically
    config.setOperationsSorter("alpha"); // Sort operations alphabetically

    return config;
  }
}
