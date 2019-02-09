package classes.classes;


public class User {
   String username;
   String firstName;
   String lastName;
   String password;
   UserRole role;
   boolean activeStatus;

   User(String username,String firstName,String lastName,String password,UserRole role){
      this.username = username;
      this.firstName = firstName;
      this.lastName = lastName;
      this.password = password;
      this.role = role;
      this.activeStatus = true;
   }

}
