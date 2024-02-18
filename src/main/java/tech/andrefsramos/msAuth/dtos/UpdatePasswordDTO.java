package tech.andrefsramos.msAuth.dtos;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatePasswordDTO {
    
    @Digits(integer = 10, fraction = 0, message = "The id field must be a number and have at most 10 digits.")
    private Long id;

    @NotBlank(message = "The password field is mandatory.")
    @Size(message = "The password field must contain a minimum of 6 characters and a maximum of 20 characters.", min = 6, max = 20)
    private String password;
}
