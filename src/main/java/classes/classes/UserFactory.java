package classes.classes;

import classes.interfaces.IUserFactory;
import classes.classes.User;

public class UserFactory implements IUserFactory{
   @Override
   public User create(String username,String firstName,String lastName,String password,UserRole role) {
      User user = new User(username,firstName,lastName,password,role);
      return user;
   }
}
