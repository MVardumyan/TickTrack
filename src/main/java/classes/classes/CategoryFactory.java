package classes.classes;

import classes.interfaces.ICategoryFactory;

public class CategoryFactory implements ICategoryFactory{
   @Override
   public Category create(String name) {
      Category category = new Category(name);
      return category;
   }
}
