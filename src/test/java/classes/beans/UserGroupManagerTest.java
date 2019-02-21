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
import static ticktrack.proto.Msg.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TickTrackContext.class)
class UserGroupManagerTest {
    private static IUserGroupManager groupManager;

    @BeforeAll
    static void initContext() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
        groupManager = (IUserGroupManager) context.getBean("GroupMng");
    }

    @Test
    void createAndDeleteUserGroup() {
        groupManager.createUserGroup("testGroup1");

        UserGroup group = groupManager.get("testGroup1");
        assertNotNull(group);
        assertEquals("testGroup1", group.getName());

        groupManager.deleteUserGroup("testGroup1");

        assertNull(groupManager.get("testGroup1"));
    }

    @Test
    void createAndUpdateUserGroup() {
        groupManager.createUserGroup("testGroup2");

        UserGroup group = groupManager.get("testGroup2");
        assertNotNull(group);
        assertEquals("testGroup2", group.getName());

        groupManager.changeName(UserGroupOp.UserGroupOpUpdateRequest.newBuilder()
                .setOldName("testGroup2")
                .setNewName("testGroup3")
                .build());

        group = groupManager.get("testGroup3");
        assertNotNull(group);
        assertEquals("testGroup3", group.getName());

        groupManager.deleteUserGroup("testGroup3");

        assertNull(groupManager.get("testGroup3"));
    }
}