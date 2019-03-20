package ticktrack.repositories;

import org.springframework.data.repository.CrudRepository;
import ticktrack.entities.PasswordLink;

public interface PasswordLinkRepository extends CrudRepository<PasswordLink, Long> {
}
