package ticktrack.repositories;

import ticktrack.entities.Ticket;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
   //Optional<Ticket> findByID(long ID);

}
