package classes.interfaces;

import classes.entities.UserGroup;
import classes.entities.User;

import java.util.Set;

public interface IUserGroupManager {
   void create(String name);

   void migrateTo(String name, Set<User> members);

   void changeName(String oldName, String newName);

   UserGroup get(String name);

   Iterable<UserGroup> getAll();
}
