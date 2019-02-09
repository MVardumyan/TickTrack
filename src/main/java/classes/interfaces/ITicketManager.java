package classes.interfaces;

import classes.classes.*;

import java.sql.Timestamp;

public interface ITicketManager {
   Ticket create(String summary, String description, TicketPriority priority, Category category);
   Ticket update(Ticket ticket,TicketPriority priority,User asignee,Category category,Comment comment,String summary,String description,
                 TicketStatus status,Timestamp openDate,User creator,String resolution,Timestamp deadline);
}
