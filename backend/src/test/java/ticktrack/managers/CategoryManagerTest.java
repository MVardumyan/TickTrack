package ticktrack.managers;

import ticktrack.entities.Category;
import ticktrack.interfaces.ICategoryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.repositories.CategoryRepository;

import static org.junit.jupiter.api.Assertions.*;
import static ticktrack.proto.Msg.CategoryOp;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CategoryManagerTest {

   @Autowired
   private ICategoryManager categoryManager;
   @Autowired
   private CategoryRepository categoryRepository;

   @Test
   void createAndDeactivateCategory() {
      categoryManager.createCategory("testCategory1");

      Category category = categoryManager.get("testCategory1");
      assertNotNull(category);
      assertEquals("testCategory1", category.getName());

      categoryManager.deactivateCategory("testCategory1");

      category = categoryManager.get("testCategory1");
      assertTrue(category.isDeactivated());
   }

   @Test
   void createAndUpdateCategory() {
      categoryManager.createCategory("testCategory2");

      Category category = categoryManager.get("testCategory2");
      assertNotNull(category);
      assertEquals("testCategory2", category.getName());

      categoryManager.changeName(CategoryOp.CategoryOpUpdateRequest.newBuilder()
         .setOldName("testCategory2")
         .setNewName("testCategory3")
         .build());

      category = categoryManager.get("testCategory3");
      assertNotNull(category);
      assertEquals("testCategory3", category.getName());
   }

   @Test
   void getAll() {
      categoryRepository.deleteAll();
      Category category1 = new Category("test1");
      categoryRepository.save(category1);
      Category category2 = new Category("test2");
      categoryRepository.save(category2);

      CategoryOp.CategoryOpGetAllResponse result = categoryManager.getAll();
      assertEquals(2, result.getCategoryNameCount());
   }
}