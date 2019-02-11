package classes.beans;

import classes.TickTrackContext;
import classes.entities.UserGroup;
import classes.interfaces.IUserGroupManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TickTrackContext.class)
class UserGroupManagerTest {
    private static IUserGroupManager groupManager;

    @BeforeAll
    static void initContext() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
        groupManager = context.getBean(IUserGroupManager.class);
    }

    @Test
    void createAndDeleteUserGroup() {
        assertTrue(groupManager.create("TestGroup1"));

        UserGroup group = groupManager.get("TestGroup1");
        assertNotNull(group);
        assertEquals("TestGroup1", group.getName());

        assertTrue(groupManager.delete("TestGroup1"));
    }

    @Test
    void createAndUpdateUserGroup() {
        assertTrue(groupManager.create("TestGroup2"));
        assertTrue(groupManager.changeName("TestGroup2", "TestGroup3"));

        UserGroup group = groupManager.get("TestGroup3");
        assertNotNull(group);
        assertEquals("TestGroup3", group.getName());

        assertTrue(groupManager.delete("TestGroup3"));
    }
}