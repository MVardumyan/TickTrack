package classes.classes;

import classes.interfaces.ITicketFactory;

public class TicketFactory implements ITicketFactory{
   @Override
   public Ticket create(String summary,String description,TicketPriority priority,Category category) {
      Ticket ticket = new Ticket(summary,description,priority,category);
      return ticket;
   }

   @Override
   public Ticket open(String summary,String description,TicketPriority priority,Category category, User asignee) {
      Ticket ticket = new Ticket(summary,description,priority,category);
      ticket.setAsignee(asignee);
      return ticket;
   }

   @Override
   public void update(Ticket ticket) {

   }

   @Override
   public void cancel() {

   }
}
