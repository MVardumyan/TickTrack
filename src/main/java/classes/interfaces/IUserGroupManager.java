package classes.interfaces;

import classes.entities.UserGroup;
import classes.entities.User;

import java.util.Set;

public interface IUserGroupManager {
   boolean create(String name);

   boolean changeName(String oldName, String newName);

   boolean delete(String name);

   UserGroup get(String name);

   Iterable<UserGroup> getAll();
}
