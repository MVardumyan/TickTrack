package classes.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class Comment{
   @Column(nullable =  false)
   @Id String username;
   @Column(nullable = false)
   Timestamp timestamp;
   @Column(nullable = false)
   String text;

   @ManyToOne
   Ticket ticket;

   public Comment(String username, Timestamp timestamp, String text){
      this.username = username;
      this.timestamp = timestamp;
      this.text = text;
   }

   public Comment() {
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setTimestamp(Timestamp timestamp) {
      this.timestamp = timestamp;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setTicket(Ticket ticket) {
      this.ticket = ticket;
   }

   public String getUsername() {
      return username;
   }

   public Timestamp getTimestamp() {
      return timestamp;
   }

   public String getText() {
      return text;
   }

   public Ticket getTicket() {
      return ticket;
   }
}
