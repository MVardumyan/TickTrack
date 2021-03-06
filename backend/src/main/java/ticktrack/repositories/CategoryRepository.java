package ticktrack.repositories;

import ticktrack.entities.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}
