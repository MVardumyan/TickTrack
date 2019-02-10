package classes.classes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
@Embeddable
public class Category implements Serializable{
   @Column(nullable = false)
   @EmbeddedId String name;

   @OneToMany
   List<Ticket> ticketList;

   Category(String name){
      this.name = name;
   }
}
