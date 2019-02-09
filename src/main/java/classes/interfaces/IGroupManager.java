package classes.interfaces;

import classes.classes.Group;
import classes.classes.User;

import java.util.List;

public interface IGroupManager {
   Group create(String name, List<User> members);
}
