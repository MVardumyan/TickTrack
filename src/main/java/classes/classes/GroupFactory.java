package classes.classes;

import classes.interfaces.IGroupFactory;

import java.util.List;

public class GroupFactory implements IGroupFactory{
   @Override
   public Group create(String name,List<User> members) {
      Group group = new Group(name,members);
      return group;
   }
}
