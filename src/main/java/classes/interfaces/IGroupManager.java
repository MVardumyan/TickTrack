package classes.interfaces;

import classes.entities.Group;
import classes.entities.User;

import java.util.List;

public interface IGroupManager {
   void create(String name);

   void migrateTo(String name, List<User> members);

   void changeName(String oldName, String newName);

   Group get(String name);

   Iterable<Group> getAll();
}
