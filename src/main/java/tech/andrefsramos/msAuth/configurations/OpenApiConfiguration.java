package tech.andrefsramos.msAuth.configurations;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("MS-AUTH")
                        .description("Microservice for user authentication with spring security and JWT token.")
                        .version("V1")
                        .termsOfService("https://www.jusbrasil.com.br/modelos-pecas/modelo-termos-e-condicoes-para-site-ou-app/784909844")
                        .license( new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")
                        )
                );
    }
}
