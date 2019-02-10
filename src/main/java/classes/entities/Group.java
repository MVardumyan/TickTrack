package classes.entities;


import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class Group{
   @Column
   @Id
   private String name;

   @OneToMany
   private List<User> members;

   public Group(String name,List<User> members){
      this.name = name;
      this.members = members;
   }

   public Group() {
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public List<User> getMembers() {
      return members;
   }
}
