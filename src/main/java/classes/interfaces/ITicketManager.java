package classes.interfaces;

import classes.classes.Category;
import classes.classes.Ticket;
import classes.classes.TicketPriority;
import classes.classes.User;

public interface ITicketManager {
   Ticket create(String summary, String description, TicketPriority priority, Category category);
   Ticket open(String summary,String description,TicketPriority priority,Category category, User asignee);
   void update(Ticket ticket);
   void cancel();
}
