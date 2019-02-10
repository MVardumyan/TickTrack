package classes.entities;

import classes.enums.UserRole;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class User{
   @Column(nullable = false)
   @Id
   private String username;
   @Column(nullable = false)
   private String firstName;
   @Column(nullable = false)
   private String lastName;
   @Column(nullable = false)
   private String password;
   @Column(nullable = false)
   private UserRole role;
   @Column
   private boolean activeStatus;

   @ManyToOne
   private Group group;

   @OneToMany
   private List<Ticket> ticketList;

   public User(String username,String firstName,String lastName,String password,UserRole role){
      this.username = username;
      this.firstName = firstName;
      this.lastName = lastName;
      this.password = password;
      this.role = role;
      this.activeStatus = true;
   }

   public User() {
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setRole(UserRole role) {
      this.role = role;
   }

   public void setActiveStatus(boolean activeStatus) {
      this.activeStatus = activeStatus;
   }

   public void setGroup(Group group) {
      this.group = group;
   }

   public String getUsername() {
      return username;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public String getPassword() {
      return password;
   }

   public UserRole getRole() {
      return role;
   }

   public boolean isActiveStatus() {
      return activeStatus;
   }

   public Group getGroup() {
      return group;
   }

   public List<Ticket> getTicketList() {
      return ticketList;
   }
}
