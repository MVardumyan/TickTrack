package classes.classes;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
@Embeddable
public class Group implements Serializable{
   @Column
   @EmbeddedId String name;

   @OneToMany
   List<User> members;

   Group(String name,List<User> members){
      this.name = name;
      this.members = members;
   }
}
