package tech.andrefsramos.msAuth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import tech.andrefsramos.msAuth.exceptions.response.ExceptionResponse;
import tech.andrefsramos.msAuth.exceptions.InvalidJwtAuthenticationException;
import tech.andrefsramos.msAuth.security.jwt.TokenService;

import java.io.IOException;
import java.util.Date;

@Component
public class SecurityFilter extends GenericFilterBean {
    private final TokenService tokenService;

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = tokenService.resolveToken((HttpServletRequest) request);
        try {
            if (token != null && tokenService.validateToken(token)) {
                Authentication auth = tokenService.getAuthentication(token);
                if(auth != null) SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(request, response);
        } catch (InvalidJwtAuthenticationException e) {
            ObjectMapper mapper = new ObjectMapper();
            SecurityContextHolder.clearContext();
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(mapper.writeValueAsString(
                    new ExceptionResponse(
                            new Date(),
                            HttpStatus.UNAUTHORIZED,
                            401,
                            "Expired or invalid JWT token!",
                            ((HttpServletRequest) request).getRequestURI()
                    )
            ));
        }
    }
}
