package classes.interfaces;

import classes.classes.Group;
import classes.classes.User;

import java.util.List;

public interface IGroupFactory {
   Group create(String name, List<User> members);
}
