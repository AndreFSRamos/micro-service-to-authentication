package tech.andrefsramos.msAuth.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tech.andrefsramos.msAuth.enums.UserRoleEnum;
import tech.andrefsramos.msAuth.security.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{
    private static final String COMMON = UserRoleEnum.COMMON.toString();
    private static final String MANAGER = UserRoleEnum.MANAGER.toString();
    private static final String ADMIN = UserRoleEnum.ADMIN.toString();
    final SecurityFilter securityFilter;


    public SecurityConfiguration(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/swagger-ui/**","/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/v1/sign-in").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/auth/v1/validate-request-token", "/api/auth/v1/auth-by-refresh-token").hasAnyRole(ADMIN,MANAGER,COMMON)
                                .requestMatchers(HttpMethod.POST, "/api/user/v1/sign-out").hasAnyRole(ADMIN,MANAGER)
                                .requestMatchers(HttpMethod.GET, "/api/user/v1/find-all", "/api/user/v1/find-by-id/{id}").hasAnyRole(ADMIN,MANAGER)
                                .requestMatchers(HttpMethod.POST, "/api/user/v1/update-password").hasAnyRole(ADMIN)
                                .requestMatchers(HttpMethod.PUT, "/api/user/v1/update-role/{id}/{role}","/api/user/v1/update-status/{id}").hasAnyRole(ADMIN)
                                .requestMatchers(HttpMethod.DELETE, "/api/user/v1/delete/{id}").hasAnyRole(ADMIN)
                                .requestMatchers("/users").denyAll()
                                .anyRequest().authenticated()
                )
                .cors(cors -> {})
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}