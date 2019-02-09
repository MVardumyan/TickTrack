package classes.classes;

import java.sql.Timestamp;

public class Ticket {
   int ID;
   TicketPriority priority;
   User asignee;
   Category category;
   Comment comment;
   String summary;
   String description;
   TicketStatus status;
   Timestamp openDate;
   User creator;
   String resolution;
   Timestamp deadline;

   Ticket(String summary,String description,TicketPriority priority,Category category){
      this.summary = summary;
      this.description = description;
      this.priority = priority;
      this.category = category;
   }
}
