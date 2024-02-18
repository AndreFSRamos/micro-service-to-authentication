package tech.andrefsramos.msAuth.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tech.andrefsramos.msAuth.dtos.UpdatePasswordDTO;
import tech.andrefsramos.msAuth.dtos.UserDTO;
import tech.andrefsramos.msAuth.dtos.UserSignOutDTO;
import tech.andrefsramos.msAuth.entities.User;
import tech.andrefsramos.msAuth.enums.UserRoleEnum;
import tech.andrefsramos.msAuth.exceptions.AccessExcption;
import tech.andrefsramos.msAuth.exceptions.ConstrainException;
import tech.andrefsramos.msAuth.exceptions.ResourceNotFoundException;
import tech.andrefsramos.msAuth.repositories.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
       return userRepository.findByUserNameAndEnabledIsTrue(username).orElseThrow(()-> new UsernameNotFoundException("Username " + username + " not found!"));
    }

    public ResponseEntity<?> signOut(UserSignOutDTO userDTO) {
        userRepository.findByUserName(userDTO.getUserName()).ifPresent(user -> {
            throw new ConstrainException("User informed already exists.");
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDTO(userRepository.save(userDTO.updateEntity(new User(), getSessionUser()))));
    }

    public ResponseEntity<List<UserDTO>> findAllUsers() {
        return ResponseEntity.ok().body(userRepository.findAll().stream().map(UserDTO::new).toList());
    }

    public ResponseEntity<?> findUserBybId(Long id) {
        return userRepository.findById(id).map(user -> ResponseEntity.ok().body(new UserDTO(user))).orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    public ResponseEntity<?> updatePassword(UpdatePasswordDTO updatePasswordDTO){
        return userRepository.findById(updatePasswordDTO.getId()).map(user -> {
            user.setPassword(new BCryptPasswordEncoder().encode(updatePasswordDTO.getPassword()));
            userRepository.save(updateUpdateFields(user));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public ResponseEntity<?> updateStatus(Long id){
        return userRepository.findByIdAndIdDifferentOne(id).map(user -> {
            user.setEnabled(!user.getEnabled());
            userRepository.save(updateUpdateFields(user));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }).orElseThrow(() -> getException(id, "Changing this user status is not allowed."));
    }

    public ResponseEntity<?> updateRole(Long id, String role){
        UserRoleEnum userRoleEnum = UserRoleEnum.fromString(role);
        if(userRoleEnum == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role, only [COMMON | MANAGER | ADMIN]");
        return userRepository.findByIdAndIdDifferentOne(id).map(user -> {
            user.setRole(userRoleEnum);
            userRepository.save(updateUpdateFields(user));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }).orElseThrow(() -> getException(id, "Changing this user's role is not allowed."));
    }

    public ResponseEntity<?> deleteById(Long id){
        return userRepository.findByIdAndIdDifferentOne(id)
                .map(user -> {
                    userRepository.deleteById(user.getId());
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                })
                .orElseThrow(() -> getException(id, "It is not allowed to delete this user."));
    }

    private Long getSessionUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .filter(principal -> principal instanceof User)
                .map(principal -> ((User) principal).getId())
                .orElseThrow(() -> new ResourceNotFoundException("Administrator user has not been localized."));
    }

    private RuntimeException getException(Long id, String textErrorAccess){
        return id == 1L ? new AccessExcption(textErrorAccess) : new ResourceNotFoundException("User not found!");
    }

    private User updateUpdateFields(User user){
        user.setSystemIdUserUpdate(getSessionUser());
        user.setSystemDateUpdate(new Date());
        return user;
    }
}
