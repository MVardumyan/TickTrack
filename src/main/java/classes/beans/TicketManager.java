package classes.beans;

import classes.entities.*;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.interfaces.ITicketManager;

import java.sql.Timestamp;

public class TicketManager implements ITicketManager {
   @Override
   public boolean create(String summary, String description, TicketPriority priority, Category category) {
      Ticket ticket = new Ticket(summary,description,priority,category);
      return true;
   }

   @Override
   public boolean updateContent(long ticket_id, String parameterName, String value) {
      return false;
   }

   @Override
   public boolean updateStatus(long ticket_id, TicketStatus newStatus) {
      return false;
   }

   @Override
   public boolean updateCategory(long ticket_id, Category newCategory) {
      return false;
   }

   @Override
   public boolean assign(long ticket_id, User user) {
      return false;
   }

   @Override
   public Ticket get(long ticket_id) {
      return null;
   }

   @Override
   public Iterable<Ticket> getAll() {
      return null;
   }

//   @Override
//   public Ticket update(Ticket ticket, TicketPriority priority, User asignee, Category category, Comment comment, String summary,
//                        String description, TicketStatus status, Timestamp openDate, User creator, String resolution, Timestamp deadline){
//      ticket.setAssignee(asignee);
//      ticket.setPriority(priority);
//      ticket.setCategory(category);
//      ticket.setComment(comment);
//      ticket.setSummary(summary);
//      ticket.setDescription(description);
//      ticket.setStatus(status);
//      ticket.setOpenDate(openDate);
//      ticket.setCreator(creator);
//      ticket.setResolution(resolution);
//      ticket.setDeadline(deadline);
//
//      return ticket;
//   }

}
