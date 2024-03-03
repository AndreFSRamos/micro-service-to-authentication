package tech.andrefsramos.msAuth.repositories;

import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.andrefsramos.msAuth.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserNameAndEnabledIsTrue(String userName);

    @Query(value = "SELECT u FROM users u WHERE u.id = :id AND u.id != 1L")
    Optional<User> findByIdAndIdDifferentOne(Long id);
}