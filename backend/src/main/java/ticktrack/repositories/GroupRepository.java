package ticktrack.repositories;

import ticktrack.entities.UserGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<UserGroup, Long> {
    Optional<UserGroup> findByName(String name);

    boolean existsByName(String name);
}
