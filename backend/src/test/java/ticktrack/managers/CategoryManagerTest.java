package ticktrack.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ticktrack.entities.Category;
import ticktrack.interfaces.ICategoryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.proto.Msg;
import ticktrack.repositories.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ticktrack.proto.Msg.CategoryOp;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Failure;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Success;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CategoryManagerTest {

   @Autowired
   private CategoryManager categoryManager;
   @Autowired
   private CategoryRepository categoryRepository;

   @BeforeEach
   void initTestData() {
      Category category1 = new Category();
      Category category2 = new Category();
      category1.setName("testCategory1");
      category2.setName("testCategory2");

      categoryRepository.save(category1);
      categoryRepository.save(category2);
   }

   @Test
   void createCategory() {
      Msg.CommonResponse commonResponse = categoryManager.createCategory("testCategory3");

      assertEquals(Success, commonResponse.getResponseType());

      Optional<Category> testCategory = categoryRepository.findByName("testCategory1");

      assertTrue(testCategory.isPresent());
      assertEquals("testCategory1", testCategory.get().getName());
   }

   @Test
   void createCategoryWithExistingName() {
      Msg.CommonResponse commonResponse = categoryManager.createCategory("testCategory1");

      assertEquals(Failure, commonResponse.getResponseType());
   }

   @Test
   void updateCategory() {
      Msg.CommonResponse commonResponse = categoryManager.changeName(CategoryOp.CategoryOpUpdateRequest.newBuilder()
              .setOldName("testCategory2")
              .setNewName("testCategory3")
              .build());

      assertEquals(Success, commonResponse.getResponseType());

      Optional<Category> testCategory = categoryRepository.findByName("testCategory3");
      assertTrue(testCategory.isPresent());
      assertEquals("testCategory3", testCategory.get().getName());
   }

   @Test
   void updateWithInvalidOldName() {
      Msg.CommonResponse commonResponse = categoryManager.changeName(CategoryOp.CategoryOpUpdateRequest.newBuilder()
              .setOldName("testCategory00")
              .setNewName("testCategory3")
              .build());

      assertEquals(Failure, commonResponse.getResponseType());
   }

   @Test
   void deactivateCategory() {
      Msg.CommonResponse commonResponse = categoryManager.deactivateCategory("testCategory1");

      assertEquals(Success, commonResponse.getResponseType());

      Optional<Category> testCategory = categoryRepository.findByName("testCategory1");
      assertTrue(testCategory.isPresent());
      assertTrue(testCategory.get().isDeactivated());
   }

   @Test
   void getCategory() {
      Category category = categoryManager.get("testCategory1");

      assertEquals("testCategory1", category.getName());
   }

   @Test
   void getInvalidCategory() {
      Category category = categoryManager.get("testCategory00");

      assertNull(category);
   }

   @Test
   void getAll() {
      CategoryOp.CategoryOpGetAllResponse result = categoryManager.getAll();
      assertEquals(2, result.getCategoryInfoCount());
   }

   @AfterEach
   void clearTestDate() {
      categoryRepository.deleteAll();
   }
}