package ticktrack.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
public class Category{
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   private long id;
   @Column(nullable = false, unique = true)
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

   public long getId() {
      return id;
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
