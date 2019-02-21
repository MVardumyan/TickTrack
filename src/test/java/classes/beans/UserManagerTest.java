package classes.beans;

import classes.TickTrackContext;
import classes.entities.User;
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
@ContextConfiguration(classes = TickTrackContext.class)
class UserManagerTest {
   private static UserManager userManager;

   @BeforeAll
   static void initContext() {
      ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
      userManager = (UserManager) context.getBean("userMng");
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
      assertEquals("user1", user.getUsername());

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
         .build());

      User user = userManager.get("user2");
      assertNotNull(user);
      assertEquals("user2", user.getUsername());

      userManager.update(Msg.UserOp.UserOpUpdateRequest.newBuilder()
         .setParamName(Msg.UserOp.UserOpUpdateRequest.ParameterName.FirstName)
         .setValue("newFirstName")
         .build());

      user = userManager.get("newFirstName");
      assertNotNull(user);
      assertEquals("newFirstName", user.getUsername());
   }

}