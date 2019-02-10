package classes.interfaces;

import classes.entities.Category;

public interface ICategoryManager {
   void create(String name);

   void delete(String name);

   void changeName(String oldName, String newName);

   Category get(String name);

   Iterable<Category> getAll();
}
