package tech.andrefsramos.msAuth.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tech.andrefsramos.msAuth.dtos.UserSignInDTO;
import tech.andrefsramos.msAuth.exceptions.InvalidJwtAuthenticationException;
import tech.andrefsramos.msAuth.repositories.UserRepository;
import tech.andrefsramos.msAuth.security.jwt.TokenService;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public ResponseEntity<?> signIn(UserSignInDTO userSignInDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userSignInDTO.getUserName(), userSignInDTO.getPassword()));
            return userRepository.findByUserNameAndEnabledIsTrue(userSignInDTO.getUserName())
                    .map(user -> ResponseEntity.ok().body(tokenService.generateAccessToken(user)))
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + userSignInDTO.getUserName() + " not found!"));
        } catch (Exception error) {
            throw new InvalidJwtAuthenticationException("invalid username/password supplied!");
        }
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        return ResponseEntity.ok().body(tokenService.refreshToken(refreshToken));
    }
}
