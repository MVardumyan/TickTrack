package ticktrack.repositories;

import ticktrack.entities.User;
import org.springframework.data.repository.CrudRepository;
import ticktrack.enums.UserRole;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
   Optional<User> findByUsername(String name);

   Iterable<User> findAllByRole(UserRole role);
}
