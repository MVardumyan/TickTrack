package ticktrack.managers;

import ticktrack.entities.UserGroup;
import ticktrack.interfaces.IUserGroupManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static ticktrack.proto.Msg.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserGroupManagerTest {

    @Autowired
    private IUserGroupManager groupManager;

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