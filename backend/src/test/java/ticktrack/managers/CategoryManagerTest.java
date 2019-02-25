package ticktrack.managers;

import ticktrack.entities.Category;
import ticktrack.interfaces.ICategoryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static ticktrack.proto.Msg.CategoryOp;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CategoryManagerTest {

   @Autowired
   private ICategoryManager categoryManager;

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
}