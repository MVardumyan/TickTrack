//package classes.beans;
//
//import classes.TickTrackContext;
//import classes.entities.Category;
//import classes.interfaces.ICategoryManager;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = TickTrackContext.class)
//class CategoryManagerTest {
//    private static ICategoryManager categoryManager;
//
//    @BeforeAll
//    static void initContext() {
//        ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
//        categoryManager = context.getBean(ICategoryManager.class);
//    }
//
//    @Test
//    void createAndDeleteCategory() {
//        assertTrue(categoryManager.categoryOperation("testCategory1"));
//
//        Category category = categoryManager.get("testCategory1");
//        assertNotNull(category);
//        assertEquals("testCategory1", category.getName());
//        assertTrue(categoryManager.delete("testCategory1"));
//    }
//
//    @Test
//    void createAndUpdateCategory() {
//        assertTrue(categoryManager.categoryOperation("testCategory2"));
//
//        assertTrue(categoryManager.changeName("testCategory2", "testCategory3"));
//        Category category = categoryManager.get("testCategory3");
//        assertNotNull(category);
//        assertEquals("testCategory3", category.getName());
//
//        assertTrue(categoryManager.delete("testCategory3"));
//    }
//
//    @Test
//    void createAndDeactivateCategory() {
//        assertTrue(categoryManager.categoryOperation("testCategory5"));
//
//        assertTrue(categoryManager.deactivate("testCategory5"));
//        Category category = categoryManager.get("testCategory5");
//        assertNotNull(category);
//        assertTrue(category.isDeactivated());
//
//        assertTrue(categoryManager.delete("testCategory5"));
//    }
//
//}