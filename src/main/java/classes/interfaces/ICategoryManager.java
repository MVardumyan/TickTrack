package classes.interfaces;

import classes.entities.Category;

public interface ICategoryManager {
   boolean create(String name);

   boolean deactivate(String name);

   boolean delete(String name);

   boolean changeName(String oldName, String newName);

   Category get(String name);

   Iterable<Category> getAll();
}
