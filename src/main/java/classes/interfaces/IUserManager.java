package classes.interfaces;

import classes.classes.User;
import classes.classes.UserRole;

public interface IUserManager {
   User create(String username,String firstName,String lastName,String password,UserRole role);
}
