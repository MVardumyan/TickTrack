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

   public void setAsignee(User asignee) {
      this.asignee = asignee;
   }

   public void setComment(Comment comment) {
      this.comment = comment;
   }

   public void setStatus(TicketStatus status) {
      this.status = status;
   }

   public void setOpenDate(Timestamp openDate) {
      this.openDate = openDate;
   }

   public void setCreator(User creator) {
      this.creator = creator;
   }

   public void setResolution(String resolution) {
      this.resolution = resolution;
   }

   public void setDeadline(Timestamp deadline) {
      this.deadline = deadline;
   }
}
