package classes.interfaces;

import classes.entities.User;
import classes.enums.UserRole;

public interface IUserManager {
   User create(String username,String firstName,String lastName,String password,UserRole role);
}
