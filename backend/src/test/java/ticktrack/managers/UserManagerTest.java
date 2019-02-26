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

        Msg.UserOp.UserOpGetByCriteriaRequest request = Msg.UserOp.UserOpGetByCriteriaRequest.newBuilder()
                .setCriteria(Msg.UserOp.UserOpGetByCriteriaRequest.Criteria.RegularUser)
                .build();

        Msg.UserOp.UserOpGetResponse result = userManager.getByCriteria(request);

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

/*
import ticktrack.TickTrackContext;
import ticktrack.entities.User;
import ticktrack.interfaces.IUserManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.proto.Msg;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(ticktrack = TickTrackContext.class)
class UserManagerTest {
   private static IUserManager userManager;

   @BeforeAll
   static void initContext() {
      ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
      userManager = (IUserManager) context.getBean("userMng");
   }

   @Test
   void createAndDeactivateUser() {
      userManager.create(Msg.UserOp.UserOpCreateRequest.newBuilder()
         .setEmail("someone@gmail.com")
         .setFirstname("some")
         .setLastname("one")
         .setPassword("1111")
         .setRole(Msg.UserRole.RegularUser)
         .setUsername("user1")
         .build());

      User user = userManager.get("user1");
      assertNotNull(user);
      assertEquals("some", user.getFirstName());

      userManager.deactivate(Msg.UserOp.UserOpDeactivateRequest.newBuilder()
         .setUsername("user1")
         .build());

      user = userManager.get("user1");
      assertFalse(user.isActive());
   }

   @Test
   void createAndUpdateUser() {
      userManager.create(Msg.UserOp.UserOpCreateRequest.newBuilder()
         .setUsername("user2")
         .setRole(Msg.UserRole.BusinessUser)
         .setPassword("2222")
         .setFirstname("hisFirstName")
         .setLastname("hisLastName")
         .setEmail("thisisemail@gmail.com")
         .build());

      User user = userManager.get("user2");
      assertNotNull(user);
      assertEquals("hisFirstName", user.getFirstName());

      userManager.update(Msg.UserOp.UserOpUpdateRequest.newBuilder()
         .setParamName(Msg.UserOp.UserOpUpdateRequest.ParameterName.FirstName)
         .setValue("newFirstName")
         .build());

      user = userManager.get("newFirstName");
      assertNotNull(user);
      assertEquals("newFirstName", user.getUsername());
   }

}*/