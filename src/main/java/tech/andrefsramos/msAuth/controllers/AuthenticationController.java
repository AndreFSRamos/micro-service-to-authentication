package tech.andrefsramos.msAuth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.andrefsramos.msAuth.dtos.*;
import tech.andrefsramos.msAuth.services.AuthenticationService;

@RestController
@RequestMapping("/api/auth/v1")
@Tag(name = "Auth", description = "Endpoints for authentication users.")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Validate a token",
            description = "Validate a token, endpoint used by other microservices to validate the request token e authorized only for ADMIN, MANAGER e COMMON.",
            tags = {"Auth"}, responses = {
            @ApiResponse(description = "Success", responseCode = "204", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @GetMapping("/validate-request-token")
    public ResponseEntity<?> tokenValidator() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Authentication by refresh token", description = "Authentication by refresh token, authorized only for ADMIN, MANAGER e COMMON.",
            tags = {"Auth"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = SignInResponseDTO.class))),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @GetMapping("/auth-by-refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
    }

    @Operation(summary = "Authentication by username/password", description = "Authentication by username/password.",
            tags = {"Auth"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = SignInResponseDTO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody UserSignInDTO userSignInDTO) {
        return this.authenticationService.signIn(userSignInDTO);
    }
}
