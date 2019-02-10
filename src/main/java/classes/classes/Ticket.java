package classes.classes;

import javax.jws.soap.SOAPBinding;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table
public class Ticket implements Serializable{
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   int ID;
   @Column(nullable = false)
   TicketPriority priority;
   @Column
   User asignee;
   @Column
   Comment comment;
   @Column(nullable = false)
   String summary;
   @Column(nullable = false)
   String description;
   @Column
   TicketStatus status;
   @Column
   Timestamp openDate;
   @Column
   User creator;
   @Column
   String resolution;
   @Column
   Timestamp deadline;

   @ManyToOne
   Group group;

   @ManyToOne
   User user;

   @ManyToOne
   Category category;

   @OneToMany
   List<Comment> commentList;

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

   public void setPriority(TicketPriority priority) {
      this.priority = priority;
   }

   public void setCategory(Category category) {
      this.category = category;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setSummary(String summary) {
      this.summary = summary;
   }
}
