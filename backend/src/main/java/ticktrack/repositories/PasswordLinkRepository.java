package ticktrack.repositories;

import org.springframework.data.repository.CrudRepository;
import ticktrack.entities.PasswordLink;
import ticktrack.entities.User;

public interface PasswordLinkRepository extends CrudRepository<PasswordLink, Long> {
    boolean existsByUser(User user);
}
