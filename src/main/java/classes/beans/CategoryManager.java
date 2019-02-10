package classes.beans;

import classes.entities.Category;
import classes.repositories.CategoryRepository;
import classes.interfaces.ICategoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public final class CategoryManager implements ICategoryManager {
   private CategoryRepository categoryRepository;
   private Logger logger = LoggerFactory.getLogger(CategoryManager.class);

   @Override
   public void create(String name) {
      Category category = new Category(name);
      categoryRepository.save(category);
      logger.debug("New category {} created and saved to db", name);
//      return category;
   }

   @Override
   public Category find(String name) {
      Optional<Category> byId = categoryRepository.findById(name);
      logger.trace("Query for {} category received", name);
      return byId.get();
   }

   @Override
   public void delete(String name) {
      categoryRepository.deleteById(name);
      logger.debug("Category {} deleted", name);
   }

   @Override
   public void changeName(String oldName, String newName) {
      categoryRepository.findById(oldName).ifPresent(category -> {
         category.setName(newName);
         categoryRepository.save(category);
      });

      logger.debug("Category {} updated to {}", oldName, newName);
   }

   @Override
   public Iterable<Category> getAll() {
      return categoryRepository.findAll();
   }
}
