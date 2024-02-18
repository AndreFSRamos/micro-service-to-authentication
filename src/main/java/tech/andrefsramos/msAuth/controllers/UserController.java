package tech.andrefsramos.msAuth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.andrefsramos.msAuth.dtos.UpdatePasswordDTO;
import tech.andrefsramos.msAuth.dtos.UserDTO;
import tech.andrefsramos.msAuth.dtos.UserSignOutDTO;
import tech.andrefsramos.msAuth.services.UserService;

@RestController
@RequestMapping("/api/user/v1")
@Tag(name = "User", description = "Endpoints for managing users.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Find all users", description = "Find all users, authorized only for ADMIN and MANAGER.",
            tags = {"User"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserDTO.class)
                                    )
                            )
                    }
            ),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @GetMapping("/find-all")
    public ResponseEntity<?> findAllUsers() {
        return userService.findAllUsers();
    }

    @Operation(summary = "Find users by id", description = "Find users by id, authorized only for ADMIN and MANAGER.",
            tags = {"User"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(description = "Unauthorized", responseCode = "204", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<?> findUserById(@PathVariable(value = "id") Long id) {
        return userService.findUserBybId(id);
    }

    @Operation(summary = "Create new user", description = "Create new user, authorized only for ADMIN and MANAGER.",
            tags = {"User"}, responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(@Valid @RequestBody UserSignOutDTO userSignOutDTO) {
        return this.userService.signOut(userSignOutDTO);
    }

    @Operation(summary = "Change password a user", description = "Change password a user, authorized only for ADMIN.",
            tags = {"User"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return userService.updatePassword(updatePasswordDTO);
    }

    @Operation(summary = "Update status a user", description = "Update status a user, authorized only for ADMIN.",
            tags = {"User"}, responses = {
            @ApiResponse(description = "Success", responseCode = "204", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable(value = "id") Long id) {
        return userService.updateStatus(id);
    }

    @Operation(summary = "Update role a user",
            description = "Update role a user, authorized only for ADMIN and only COMMON, MANAGER and ADMIN are accepted in the role field",
            tags = {"User"}, responses = {
            @ApiResponse(description = "Success", responseCode = "204", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @PutMapping("/update-role/{id}/{role}")
    public ResponseEntity<?> updateRole(@PathVariable(value = "id") Long id, @PathVariable(value = "role") String role) {
        return userService.updateRole(id, role);
    }

    @Operation(summary = "Delete a user", description = "Delete a user, authorized only for ADMIN.",
            tags = {"User"}, responses = {
            @ApiResponse(description = "Success", responseCode = "204", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
    }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") Long id) {
        return userService.deleteById(id);
    }
}
