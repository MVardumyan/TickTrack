package ticktrack.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.entities.User;
import ticktrack.entities.UserGroup;
import ticktrack.enums.Gender;
import common.enums.UserRole;
import ticktrack.proto.Msg;
import ticktrack.repositories.GroupRepository;
import ticktrack.repositories.PasswordLinkRepository;
import ticktrack.repositories.UserRepository;
import common.helpers.PasswordHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Failure;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Success;

import java.sql.Timestamp;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserManagerTest {
    @Autowired
    UserManager userManager;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordLinkRepository passwordLinkRepository;

    @BeforeEach
    void initTestData() {
        UserGroup testGroup = new UserGroup();
        testGroup.setName("testGroup");
        groupRepository.save(testGroup);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setFirstName("Jane");
        user1.setLastName("Doe");
        user1.setEmail("jane@somemail.com");
        user1.setPassword("password");
        user1.setGender(Gender.Female);
        user1.setRole(UserRole.RegularUser);
        user1.setActiveStatus(true);
        user1.setRegistrationTime(new Timestamp(System.currentTimeMillis()));

        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setFirstName("John");
        user2.setLastName("Smith");
        user2.setEmail("john@somemail.com");
        user2.setGender(Gender.Male);
        user2.setPassword("password");
        user2.setRole(UserRole.RegularUser);
        user2.setActiveStatus(true);
        user2.setRegistrationTime(new Timestamp(System.currentTimeMillis()));

        userRepository.save(user2);
    }

    @Test
    void createUser() {
        Msg.UserOp.UserOpCreateRequest request = Msg.UserOp.UserOpCreateRequest.newBuilder()
                .setUsername("user3")
                .setFirstname("John")
                .setLastname("Smith")
                .setEmail("someone@somemail.com")
                .setPassword("password")
                .setGender(Msg.UserOp.Gender.Male)
                .setRole(Msg.UserRole.BusinessUser)
                .build();

        Msg.CommonResponse commonResponse = userManager.create(request);

        assertEquals(Success, commonResponse.getResponseType());

        Optional<User> result = userRepository.findByUsername("user3");
        assertTrue(result.isPresent());
        User user = result.get();

        assertTrue(user.isActive());
        assertEquals("John", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("someone@somemail.com", user.getEmail());
        assertEquals(UserRole.BusinessUser, user.getRole());
    }

    @Test
    void createUserWithExistingUsername() {
        Msg.UserOp.UserOpCreateRequest request = Msg.UserOp.UserOpCreateRequest.newBuilder()
                .setUsername("user1")
                .setFirstname("John")
                .setLastname("Smith")
                .setEmail("someone@somemail.com")
                .setPassword("password")
                .setGender(Msg.UserOp.Gender.Male)
                .setRole(Msg.UserRole.BusinessUser)
                .build();

        Msg.CommonResponse commonResponse = userManager.create(request);

        assertEquals(Failure, commonResponse.getResponseType());
    }

    @Test
    void createUserWithGroup() {
        Msg.UserOp.UserOpCreateRequest request = Msg.UserOp.UserOpCreateRequest.newBuilder()
                .setUsername("user3")
                .setFirstname("John")
                .setLastname("Smith")
                .setEmail("someone@somemail.com")
                .setPassword("password")
                .setGender(Msg.UserOp.Gender.Male)
                .setRole(Msg.UserRole.RegularUser)
                .setGroup("testGroup")
                .build();

        Msg.CommonResponse commonResponse = userManager.create(request);

        assertEquals(Success, commonResponse.getResponseType());

        Optional<User> result = userRepository.findByUsername("user3");
        assertTrue(result.isPresent());
        User user = result.get();

        assertTrue(user.isActive());
        assertEquals(UserRole.RegularUser, user.getRole());
        assertEquals("testGroup", user.getGroup().getName());
    }

    @Test
    void creteUserWithInvalidGroup() {
        Msg.UserOp.UserOpCreateRequest request = Msg.UserOp.UserOpCreateRequest.newBuilder()
                .setUsername("user3")
                .setFirstname("John")
                .setLastname("Smith")
                .setEmail("someone@somemail.com")
                .setPassword("password")
                .setGender(Msg.UserOp.Gender.Male)
                .setRole(Msg.UserRole.RegularUser)
                .setGroup("group000")
                .build();

        Msg.CommonResponse commonResponse = userManager.create(request);

        assertEquals(Failure, commonResponse.getResponseType());
    }

    @Test
    void deactivateUser() {
        Msg.CommonResponse commonResponse = userManager.deactivate("user1");

        assertEquals(Success, commonResponse.getResponseType());

        Optional<User> result = userRepository.findByUsername("user1");
        assertTrue(result.isPresent());
        assertFalse(result.get().isActive());
    }

    @Test
    void deactivateInvalidUser() {
        Msg.CommonResponse commonResponse = userManager.deactivate("user3");

        assertEquals(Failure, commonResponse.getResponseType());
    }

    @Test
    void deactivateDeactivatedUser() {
        Optional<User> result = userRepository.findByUsername("user1");
        assertTrue(result.isPresent());
        User user = result.get();
        user.setActiveStatus(false);
        userRepository.save(user);

        Msg.CommonResponse commonResponse = userManager.deactivate("user1");

        assertEquals(Failure, commonResponse.getResponseType());
    }

    @Test
    void getUser() {
        Msg.UserOp.UserOpGetResponse response = userManager.get("user1");

        assertEquals(1, response.getUserInfoCount());
    }

    @Test
    void getInvalidUser() {
        Msg.UserOp.UserOpGetResponse response = userManager.get("user000");

        assertEquals(0, response.getUserInfoCount());
    }

    @Test
    void GetByRole() {
        Msg.UserOp.UserOpGetByRoleRequest request = Msg.UserOp.UserOpGetByRoleRequest.newBuilder()
                .setCriteria(Msg.UserOp.UserOpGetByRoleRequest.Criteria.RegularUser)
                .build();

        Msg.UserOp.UserOpGetResponse result = userManager.getByRole(request,1,10);

        assertEquals(2, result.getUserInfoCount());
        assertEquals("user1", result.getUserInfo(0).getUsername());
        assertEquals("user2", result.getUserInfo(1).getUsername());
    }

    @Test
    void validateLogin() {
        Msg.LoginRequest request = Msg.LoginRequest.newBuilder()
                .setUsername("user1")
                .setPassword(PasswordHandler.encode("password"))
                .build();

        Msg.CommonResponse result = userManager.validateLoginInformation(request);
        assertEquals(Success, result.getResponseType());
    }

    @AfterEach
    void deleteTestData() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }


}