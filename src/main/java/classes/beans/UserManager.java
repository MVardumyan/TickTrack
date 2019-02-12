package classes.beans;

import classes.entities.User;
import classes.enums.UserRole;
import classes.interfaces.IUserManager;

public class UserManager implements IUserManager {
   @Override
   public boolean create(String username, String firstName, String lastName, String password, UserRole role) {
      User user = new User(username,firstName,lastName,password,role);
      return true;
   }

   @Override
   public boolean update(String username, String parameterName, String value) {
      return false;
   }

   @Override
   public boolean parameterPassword(String username, String password) {
      return false;
   }

   @Override
   public boolean deactivate(String username) {
      return false;
   }

   @Override
   public User get(String username) {
      return null;
   }
}
