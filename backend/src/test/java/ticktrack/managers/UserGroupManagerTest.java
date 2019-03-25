package ticktrack.managers;

import common.enums.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ticktrack.entities.User;
import ticktrack.entities.UserGroup;
import ticktrack.enums.Gender;
import ticktrack.interfaces.IUserGroupManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.repositories.GroupRepository;
import ticktrack.repositories.UserRepository;

import java.sql.Timestamp;
import java.util.Optional;

import static ticktrack.proto.Msg.*;

import static org.junit.jupiter.api.Assertions.*;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Failure;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Success;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserGroupManagerTest {

    @Autowired
    private UserGroupManager groupManager;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void initTestData() {
        UserGroup group = new UserGroup();
        group.setName("group1");

        groupRepository.save(group);
    }

    @Test
    void createUserGroup() {
        CommonResponse commonResponse = groupManager.createUserGroup("group2");

        assertEquals(Success, commonResponse.getResponseType());

        Optional<UserGroup> result = groupRepository.findByName("group2");
        assertTrue(result.isPresent());
        assertEquals("group2", result.get().getName());
    }

    @Test
    void createGroupWithExistingName() {
        CommonResponse commonResponse = groupManager.createUserGroup("group1");

        assertEquals(Failure, commonResponse.getResponseType());
    }

    @Test
    void deleteUserGroup() {
        CommonResponse commonResponse = groupManager.deleteUserGroup("group1");

        assertEquals(Success, commonResponse.getResponseType());

        Optional<UserGroup> result = groupRepository.findByName("group1");

        assertFalse(result.isPresent());
    }

    @Test
    void deleteGroupWithUsers() {
        User testUser = new User();
        testUser.setUsername("john");
        testUser.setActiveStatus(true);
        testUser.setFirstName("john");
        testUser.setLastName("doe");
        testUser.setEmail("someone@gmail.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.BusinessUser);
        testUser.setGender(Gender.Male);
        testUser.setRegistrationTime(new Timestamp(System.currentTimeMillis()));

        testUser.setGroup(
                groupRepository.findByName("group1").get()
        );
        userRepository.save(testUser);

        CommonResponse commonResponse = groupManager.deleteUserGroup("group1");

        assertEquals(Failure, commonResponse.getResponseType());
    }

    @Test
    void updateUserGroup() {

        CommonResponse commonResponse = groupManager.changeName(UserGroupOp.UserGroupOpUpdateRequest.newBuilder()
                .setOldName("group1")
                .setNewName("group2")
                .build());

        assertEquals(Success, commonResponse.getResponseType());

        Optional<UserGroup> result = groupRepository.findByName("group2");

        assertTrue(result.isPresent());
        assertEquals("group2", result.get().getName());
    }

    @Test
    void updateNonExistingGroup() {
        CommonResponse commonResponse = groupManager.changeName(UserGroupOp.UserGroupOpUpdateRequest.newBuilder()
                .setOldName("group00")
                .setNewName("group2")
                .build());

        assertEquals(Failure, commonResponse.getResponseType());
    }

    @AfterEach
    void clearTestData() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }
}