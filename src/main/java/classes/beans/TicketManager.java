package classes.beans;

import classes.entities.*;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.interfaces.ITicketManager;

import java.sql.Timestamp;

public class TicketManager implements ITicketManager {
   @Override
   public Ticket create(String summary, String description, TicketPriority priority, Category category) {
      Ticket ticket = new Ticket(summary,description,priority,category);
      return ticket;
   }

   @Override
   public Ticket update(Ticket ticket, TicketPriority priority, User asignee, Category category, Comment comment, String summary,
                        String description, TicketStatus status, Timestamp openDate, User creator, String resolution, Timestamp deadline){
      ticket.setAsignee(asignee);
      ticket.setPriority(priority);
      ticket.setCategory(category);
      ticket.setComment(comment);
      ticket.setSummary(summary);
      ticket.setDescription(description);
      ticket.setStatus(status);
      ticket.setOpenDate(openDate);
      ticket.setCreator(creator);
      ticket.setResolution(resolution);
      ticket.setDeadline(deadline);

      return ticket;
   }

}
