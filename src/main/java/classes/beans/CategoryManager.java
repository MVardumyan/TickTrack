package classes.beans;

import classes.entities.Category;
import classes.interfaces.ICategoryManager;

public class CategoryManager implements ICategoryManager {
   @Override
   public Category create(String name) {
      Category category = new Category(name);
      return category;
   }
}
