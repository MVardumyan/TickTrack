package classes.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class Category{
   @Column(nullable = false)
   @Id String name;

   @OneToMany
   List<Ticket> ticketList;

   public Category() {}

   public Category(String name){
      this.name = name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
