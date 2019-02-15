package classes.repositories;

import classes.entities.Ticket;
import classes.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
   Optional<Ticket> findByID(long ID);

   boolean existsByName(String name);
}
