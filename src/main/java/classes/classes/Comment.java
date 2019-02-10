package classes.classes;

import javax.persistence.*;
import java.io.Serializable;
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

   Comment(String username, Timestamp timestamp, String text){
      this.username = username;
      this.timestamp = timestamp;
      this.text = text;
   }

   public Comment() {
   }
}
