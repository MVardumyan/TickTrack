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
   public boolean create(String name) {
       if(categoryRepository.existsByName(name)) {
           logger.warn("Category {} already exists", name);
           return false;
       } else {
           Category category = new Category(name);
           categoryRepository.save(category);
           logger.debug("New category {} created and saved to db", name);
           return true;
       }
   }

    @Override
    @Transactional
    public boolean deactivate(String name) {
        Optional<Category> result  = categoryRepository.findByName(name);

        if(result.isPresent()) {
            Category category = result.get();
            category.setDeactivated(true);
            categoryRepository.save(category);

            logger.debug("Category {} deactivated", name);
            return true;
        } else {
            logger.warn("Category {} not found", name);
            return false;
        }
    }

   @Override
   @Transactional
   public boolean delete(String name) {
       Category category = get(name);

       if(category!=null) {
           if(category.getTicketList().size()==0) {
               categoryRepository.delete(category);
               logger.debug("Category {} deleted", name);
               return true;
           } else {
               logger.warn("Category {} cannot be deleted : there are tickets with this category", name);
               return false;
           }
       } else {
           return false;
       }
   }

   @Override
   @Transactional
   public boolean changeName(String oldName, String newName) {
       Category category = get(oldName);

       if(category!=null) {
           category.setName(newName);
           categoryRepository.save(category);

           logger.debug("Category {} updated to {}", oldName, newName);
           return true;
       } else {
           return false;
       }
   }

   @Override
   @Transactional
   public Category get(String name) {
      Optional<Category> result = categoryRepository.findByName(name);

      if(result.isPresent()) {
         logger.debug("Query for {} category received", name);
         return result.get();
      } else {
         logger.debug("Category {} not found", name);
         return null;
      }
   }

   @Override
   @Transactional
   public Iterable<Category> getAll() {
      return categoryRepository.findAll();
   }
}
