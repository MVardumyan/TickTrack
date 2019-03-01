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
import ticktrack.enums.UserRole;
import ticktrack.proto.Msg;
import ticktrack.repositories.GroupRepository;
import ticktrack.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    private UserGroup testGroup;

    @BeforeEach
    void initTestData() {
        testGroup = new UserGroup();
        testGroup.setName("testGroup");
        groupRepository.save(testGroup);
    }

    @Test
    void createUser() {
        Msg.UserOp.UserOpCreateRequest request = Msg.UserOp.UserOpCreateRequest.newBuilder()
                .setUsername("user1")
                .setFirstname("John")
                .setLastname("Doe")
                .setEmail("something@somemail.com")
                .setPassword("password")
                .setRole(Msg.UserRole.BusinessUser)
                .build();

        userManager.create(request);

        Optional<User> result = userRepository.findByUsername("user1");
        assertTrue(result.isPresent());
        User user = result.get();

        assertTrue(user.isActive());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("something@somemail.com", user.getEmail());
        assertEquals(UserRole.BusinessUser, user.getRole());

        userRepository.delete(user);
    }

    @Test
    void SearchByRole() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setFirstName("Jane");
        user1.setLastName("Doe");
        user1.setEmail("jane@somemail.com");
        user1.setPassword("password");
        user1.setRole(UserRole.RegularUser);

        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setFirstName("John");
        user2.setLastName("Smith");
        user2.setEmail("john@somemail.com");
        user2.setPassword("password");
        user2.setRole(UserRole.RegularUser);

        userRepository.save(user2);

        Msg.UserOp.UserOpGetByRoleRequest request = Msg.UserOp.UserOpGetByRoleRequest.newBuilder()
                .setCriteria(Msg.UserOp.UserOpGetByRoleRequest.Criteria.RegularUser)
                .build();

        Msg.UserOp.UserOpGetResponse result = userManager.getByRole(request);

        assertEquals(2, result.getUserInfoCount());
        assertEquals("user1", result.getUserInfo(0).getUsername());
        assertEquals("user2", result.getUserInfo(1).getUsername());

        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @AfterEach
    void deleteTestData() {
        groupRepository.delete(testGroup);
    }


}