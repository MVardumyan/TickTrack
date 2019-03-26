package ticktrack.entities;

import ticktrack.enums.Gender;
import common.enums.UserRole;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Table
public class User{
   @Column(nullable = false, unique = true)
   @Id
   private String username;
   @Column(nullable = false)
   private String firstName;
   @Column(nullable = false)
   private String lastName;
   @Column(nullable = false)
   @Enumerated(EnumType.ORDINAL)
   private Gender gender;
   @Column(nullable = false)
   private String password;
   @Column(nullable = false)
   private String email;
   @Column(nullable = false)
   private UserRole role;
   @Column(nullable = false)
   private Timestamp registrationTime;
   @Column
   private boolean activeStatus;
   @Column
   private Timestamp deactivationTime;

   @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
   private PasswordLink passwordLink;

   @ManyToOne
   private UserGroup group;

   @OneToMany(mappedBy = "creator")
   private List<Ticket> createdTicketList;

   @OneToMany(mappedBy = "assignee")
   private List<Ticket> assignedTicketList;

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

   public void setGender(Gender gender) {
      this.gender = gender;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setRole(UserRole role) {
      this.role = role;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public void setActiveStatus(boolean activeStatus) {
      this.activeStatus = activeStatus;
   }

   public void setRegistrationTime(Timestamp registrationTime) {
      this.registrationTime = registrationTime;
   }

   public void setDeactivationTime(Timestamp deactivationTime) {
      this.deactivationTime = deactivationTime;
   }

   public void setPasswordLink(PasswordLink passwordLink) {
      this.passwordLink = passwordLink;
   }

   public void setGroup(UserGroup group) {
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

   public String getEmail() {
      return email;
   }

   public UserRole getRole() {
      return role;
   }

   public boolean isActive() {
      return activeStatus;
   }

   public UserGroup getGroup() {
      return group;
   }

   public Gender getGender() {
      return gender;
   }

   public Timestamp getRegistrationTime() {
      return registrationTime;
   }

   public Timestamp getDeactivationTime() {
      return deactivationTime;
   }

   public PasswordLink getPasswordLink() {
      return passwordLink;
   }

   public List<Ticket> getCreatedTicketList() {
      return createdTicketList;
   }

   public List<Ticket> getAssignedTicketList() {
      return assignedTicketList;
   }
}
