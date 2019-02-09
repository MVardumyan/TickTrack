package classes.classes;

import classes.interfaces.IUserManager;

public class UserManager implements IUserManager {
   @Override
   public User create(String username,String firstName,String lastName,String password,UserRole role) {
      User user = new User(username,firstName,lastName,password,role);
      return user;
   }
}
