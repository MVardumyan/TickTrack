package classes.beans;

import classes.entities.Group;
import classes.entities.User;
import classes.interfaces.IGroupManager;

import java.util.List;

public class GroupManager implements IGroupManager {
   @Override
   public Group create(String name, List<User> members) {
      Group group = new Group(name,members);
      return group;
   }
}
