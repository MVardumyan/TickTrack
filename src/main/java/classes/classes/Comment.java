package classes.classes;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table
@Embeddable
public class Comment implements Serializable{
   @Column(nullable =  false)
   @EmbeddedId String username;
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
}
