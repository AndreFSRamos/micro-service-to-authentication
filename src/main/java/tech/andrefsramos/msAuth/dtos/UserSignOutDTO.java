package tech.andrefsramos.msAuth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tech.andrefsramos.msAuth.enums.UserRoleEnum;
import tech.andrefsramos.msAuth.entities.User;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignOutDTO {

    @NotBlank(message = "The userName field is mandatory.")
    private String userName;

    @NotBlank(message = "The password field is mandatory.")
    @Size(message = "The password field must contain a minimum of 6 characters and a maximum of 20 characters.", min = 6, max = 20)
    private String password;

    public User updateEntity(User user, Long idLoggedUser){
        user.setUserName(this.userName);
        user.setPassword(new BCryptPasswordEncoder().encode(this.password));
        user.setRole(UserRoleEnum.COMMON);
        user.setSystemIdUserInsert(idLoggedUser);
        user.setSystemDateInsert(new Date());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        return user;
    }
}
