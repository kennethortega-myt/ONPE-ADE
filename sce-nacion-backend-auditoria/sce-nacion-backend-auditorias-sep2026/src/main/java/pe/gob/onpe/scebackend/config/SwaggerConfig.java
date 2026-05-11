package pe.gob.onpe.scebackend.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class SwaggerConfig {
    @Bean
    @Primary
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("default")
            .pathsToMatch("/**")  // Incluir todos los paths en la documentación
            .addOperationCustomizer((operation, handlerMethod) -> operation) // Customización adicional si es necesario
            .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Api Base")
                .description("Descripción de la API base")
                .version("0.0.1")
                .contact(new Contact()
                    .name("Sce")
                    .url("web.onpe.gob.pe")
                    .email("contactanos@onpe.gob.pe"))
                .license(new License().name("MIT").url("www.gob.pe"))
            );
    }
}
