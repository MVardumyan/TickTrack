package classes.beans;

import classes.entities.Category;
import classes.repositories.CategoryRepository;
import classes.interfaces.ICategoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoryManager implements ICategoryManager {
   @Autowired
   private CategoryRepository categoryRepository;
   private Logger logger = LoggerFactory.getLogger(CategoryManager.class);

   @Override
   @Transactional
   public void create(String name) {
      Category category = new Category(name);
      categoryRepository.save(category);
      logger.debug("New category {} created and saved to db", name);
   }

   @Override
   @Transactional
   public void delete(String name) {
      categoryRepository.deleteById(name);
      logger.debug("Category {} deleted", name);
   }

   @Override
   @Transactional
   public void changeName(String oldName, String newName) {
      categoryRepository.findById(oldName).ifPresent(category -> {
         category.setName(newName);
         categoryRepository.save(category);
      });

      logger.debug("Category {} updated to {}", oldName, newName);
   }

   @Override
   @Transactional
   public Category get(String name) {
      Optional<Category> byId = categoryRepository.findById(name);
      logger.trace("Query for {} category received", name);
      return byId.get();
   }

   @Override
   @Transactional
   public Iterable<Category> getAll() {
      return categoryRepository.findAll();
   }
}
