package vsu.ru.market.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vsu.ru.market.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findBySecretKey(String secretKey);
}
