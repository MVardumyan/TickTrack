package classes.interfaces;

import classes.entities.Category;

public interface ICategoryManager {
   void create(String name);

   Category find(String name);

   void delete(String name);

   void changeName(String oldName, String newName);

   Iterable<Category> getAll();
}
