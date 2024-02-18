package tech.andrefsramos.msAuth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignInDTO {

    @NotBlank(message = "The userName field is mandatory.")
    private String userName;

    @NotBlank(message = "The password field is mandatory.")
    @Size(message = "The password field must contain a minimum of 6 characters and a maximum of 20 characters.", min = 6, max = 20)
    private String password;

}