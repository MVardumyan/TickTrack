package classes.classes;

import classes.interfaces.ICategoryManager;

public class CategoryManager implements ICategoryManager {
   @Override
   public Category create(String name) {
      Category category = new Category(name);
      return category;
   }
}
