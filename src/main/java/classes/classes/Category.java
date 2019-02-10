package classes.classes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
public class Category{
   @Column(nullable = false)
   @Id String name;

   @OneToMany
   List<Ticket> ticketList;

   public Category(String name){
      this.name = name;
   }

   public Category() {
   }
}
