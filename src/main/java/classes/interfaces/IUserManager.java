package classes.interfaces;

import classes.entities.User;
import classes.enums.UserRole;

public interface IUserManager {
   boolean create(String username,String firstName,String lastName,String password,UserRole role);

   boolean update(String username, String parameterName, String value);

   boolean parameterPassword(String username, String password);

   boolean deactivate(String username);

   User get(String username);

}
