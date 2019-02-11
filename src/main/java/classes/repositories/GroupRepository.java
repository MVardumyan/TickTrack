package classes.repositories;

import classes.entities.UserGroup;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<UserGroup, String> {
}
