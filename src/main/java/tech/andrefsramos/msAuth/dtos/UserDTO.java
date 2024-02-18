package tech.andrefsramos.msAuth.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import tech.andrefsramos.msAuth.entities.User;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String userName;
    private List<String> roles;
    private Boolean enable;
    private Date created;

    public UserDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        this.created = user.getSystemDateInsert();
        this.enable = user.getEnabled();
    }
}
