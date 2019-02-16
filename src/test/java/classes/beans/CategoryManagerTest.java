package classes.beans;

import classes.TickTrackContext;
import classes.entities.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static ticktrack.proto.Msg.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TickTrackContext.class)
class CategoryManagerTest {
    private static CategoryManager categoryManager;

    @BeforeAll
    static void initContext() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
        categoryManager = (CategoryManager) context.getBean("CategoryMng");
    }

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