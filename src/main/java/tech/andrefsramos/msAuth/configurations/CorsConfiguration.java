package tech.andrefsramos.msAuth.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    @Value("${cors.origenPatterns:default}")
    private String corsOrigenPatterns = "";
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allowedOrigins = corsOrigenPatterns.split(",");

        registry.addMapping("/api/auth/v1/sign-in")
                .allowedMethods("POST")
                .allowedOrigins("*")
                .allowCredentials(false);

        registry.addMapping("/api/auth/v1/auth-by-refresh-token")
                .allowedMethods("GET")
                .allowedOrigins("*")
                .allowCredentials(false);

        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD")
                .allowedOrigins(allowedOrigins)
                .allowCredentials(true);
    }
}
