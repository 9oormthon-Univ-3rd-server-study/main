package start.goorm.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import start.goorm.study.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
