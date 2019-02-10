package classes.classes;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
public class Group{
   @Column
   @Id String name;

   @OneToMany
   List<User> members;

   Group(String name,List<User> members){
      this.name = name;
      this.members = members;
   }

   public Group() {
   }
}
