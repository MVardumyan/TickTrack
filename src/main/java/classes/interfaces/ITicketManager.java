package classes.interfaces;

import classes.entities.*;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;

import java.sql.Timestamp;

public interface ITicketManager {
   boolean create(String summary, String description, TicketPriority priority, Category category);

   boolean updateContent(long ticket_id, String parameterName, String value);

   boolean updateStatus(long ticket_id, TicketStatus newStatus);

   boolean updateCategory(long ticket_id, Category newCategory);

   boolean assign(long ticket_id, User user);

   Ticket get(long ticket_id);

   Iterable<Ticket> getAll();
}
