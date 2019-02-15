package classes.beans;

import classes.TickTrackContext;
import classes.entities.Category;
import classes.interfaces.ICategoryManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.proto.CategoryOp;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TickTrackContext.class)
class CategoryManagerTest {
    private static ICategoryManager categoryManager;

    @BeforeAll
    static void initContext() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
        categoryManager = context.getBean(ICategoryManager.class);
    }

    @Test
    void createAndDeactivateCategory() {
        categoryManager.categoryOperation(CategoryOp.CategoryOpRequest.newBuilder()
                .setCategoryName("testCategory1")
                .setOpType(CategoryOp.CategoryOpRequest.OpType.Create)
                .build());

        Category category = categoryManager.get("testCategory1");
        assertNotNull(category);
        assertEquals("testCategory1", category.getName());

        categoryManager.categoryOperation(CategoryOp.CategoryOpRequest.newBuilder()
                .setCategoryName("testCategory1")
                .setOpType(CategoryOp.CategoryOpRequest.OpType.Deactivate)
                .build());

        category = categoryManager.get("testCategory1");
        assertTrue(category.isDeactivated());
    }

    @Test
    void createAndUpdateCategory() {
        categoryManager.categoryOperation(CategoryOp.CategoryOpRequest.newBuilder()
                .setCategoryName("testCategory2")
                .setOpType(CategoryOp.CategoryOpRequest.OpType.Create)
                .build());

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