package classes.interfaces;

import classes.entities.Group;
import classes.entities.User;

import java.util.List;

public interface IGroupManager {
   Group create(String name, List<User> members);
}
