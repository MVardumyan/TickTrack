package ticktrack.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ticktrack.entities.User;
import common.enums.UserRole;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository {
   Optional<User> findByUsername(String name);
   Page findAll(Pageable pageable);
   Iterable<User> findAllByRole(UserRole role, Pageable pageable);
}
