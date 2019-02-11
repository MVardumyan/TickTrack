package classes.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
public class Category{
   @Column(nullable = false, unique = true)
   @Id
   private String name;

   @OneToMany(mappedBy = "category")
   private Set<Ticket> ticketList;

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
