package classes.entities;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table
public class UserGroup {
    @Column(nullable = false, unique = true)
    @Id
    private String name;

   @OneToMany(mappedBy = "group")
   private Set<User> members;

   @OneToMany(mappedBy = "group")
   private Set<Ticket> tickets;

   public UserGroup(String name, Set<User> members){
      this.name = name;
      this.members = members;
   }

   public UserGroup() {
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public Set<User> getMembers() {
      return members;
   }
}
