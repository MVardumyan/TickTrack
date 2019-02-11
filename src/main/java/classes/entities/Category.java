package classes.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
public class Category{
   @Column(nullable = false, unique = true)
   @Id
   private String name;
   @Column
   private boolean deactivated;

   @OneToMany(mappedBy = "category")
   private Set<Ticket> ticketList;

   public Category() {}

   public Category(String name){
      this.name = name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDeactivated(boolean deactivated) {
      this.deactivated = deactivated;
   }

   public String getName() {
      return name;
   }

   public boolean isDeactivated() {
      return deactivated;
   }

   public Set<Ticket> getTicketList() {
      return ticketList;
   }
}
