package classes.repositories;

import classes.entities.UserGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<UserGroup, String> {
    Optional<UserGroup> findByName(String name);

    boolean existsByName(String name);
}
