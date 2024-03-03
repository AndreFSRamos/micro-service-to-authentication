package tech.andrefsramos.msAuth.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.andrefsramos.msAuth.dtos.SignInResponseDTO;
import tech.andrefsramos.msAuth.entities.User;
import tech.andrefsramos.msAuth.exceptions.InvalidJwtAuthenticationException;
import tech.andrefsramos.msAuth.exceptions.ResourceNotFoundException;
import tech.andrefsramos.msAuth.repositories.UserRepository;

import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {
    @Value("${security.jwt.token.secret-key:secret}")
    private String SECRET_KEY = "secret";

    @Value("${security.jwt.token.access-token.expire-length:3600000}")
    private long ACCESS_TOKEN_VALIDITY_IN_MILLISECOND = 3600000; //1h

    @Value("${security.jwt.token.refresh-token.expire-length:3600000}")
    private long REFRESH_TOKEN_VALIDITY_IN_MILLISECOND = 3600000 * 3; //3h

    private Algorithm algorithm = null;

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public TokenService(UserDetailsService userDetailsService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
        algorithm = Algorithm.HMAC256(SECRET_KEY);
    }

    public SignInResponseDTO generateAccessToken(User user) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_IN_MILLISECOND);
        return new SignInResponseDTO(
                user.getUsername(),
                true,
                now,
                validity,
                getAccessToken(user, now, validity),
                getRefreshToken(user, now)
        );
    }

    public SignInResponseDTO refreshToken(String refreshToken){
        if (refreshToken.contains("Bearer "))
            refreshToken = refreshToken.substring("Bearer ".length());

       return userRepository.findByUserNameAndEnabledIsTrue(JWT.require(algorithm).build().verify(refreshToken).getSubject())
                .map(this::generateAccessToken)
                .orElseThrow(() -> new ResourceNotFoundException("Username not found!"));
    }

    private String getAccessToken(User user, Date now, Date validity) {
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(user.getUsername())
                .withClaim("id", user.getId().toString())
                .withClaim("role", user.getRole().toString())
                .withIssuer(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString())
                .sign(algorithm)
                .strip();
    }

    private String getRefreshToken(User user, Date now) {
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(new Date(new Date().getTime() + REFRESH_TOKEN_VALIDITY_IN_MILLISECOND))
                .withSubject(user.getUsername())
                .withClaim("id", user.getId().toString())
                .withClaim("role", user.getRole().toString())
                .sign(algorithm)
                .strip();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(decodedToken(token).getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY.getBytes())).build().verify(token);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) return bearerToken.substring("Bearer ".length());
        return bearerToken;
    }

    public boolean validateToken(String token) {
        try {
            return !decodedToken(token).getExpiresAt().before(new Date());
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token!");
        }
    }
}
