package classes.classes;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class User{
   @Column(nullable = false)
   @Id String username;
   @Column(nullable = false)
   String firstName;
   @Column(nullable = false)
   String lastName;
   @Column(nullable = false)
   String password;
   @Column(nullable = false)
   UserRole role;
   @Column
   boolean activeStatus;

   @ManyToOne
   Group group;

   @OneToMany
   List<Ticket> ticketList;

   User(String username,String firstName,String lastName,String password,UserRole role){
      this.username = username;
      this.firstName = firstName;
      this.lastName = lastName;
      this.password = password;
      this.role = role;
      this.activeStatus = true;
   }

   public User() {
   }
}
