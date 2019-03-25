package ticktrack.managers;

import org.junit.jupiter.api.*;
import ticktrack.entities.Category;
import ticktrack.entities.Ticket;
import ticktrack.entities.User;
import ticktrack.enums.Gender;
import ticktrack.enums.TicketPriority;
import ticktrack.enums.TicketStatus;
import common.enums.UserRole;
import ticktrack.repositories.CategoryRepository;
import ticktrack.repositories.TicketRepository;
import ticktrack.repositories.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.proto.Msg;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SearchManagerTest {
   @Autowired
   private SearchManager searchManager;
   @Autowired
   private TicketRepository ticketRepository;
   @Autowired
   private UserRepository userRepository;
   @Autowired
   private CategoryRepository categoryRepository;
   private User testUser;
   private Category testCategory;

   @BeforeEach
   void initTestData() {
      testUser = new User();
      testUser.setUsername("mik");
      testUser.setActiveStatus(true);
      testUser.setFirstName("john");
      testUser.setLastName("doe");
      testUser.setEmail("someone@gmail.com");
      testUser.setPassword("password");
      testUser.setRole(UserRole.BusinessUser);
      testUser.setGender(Gender.Male);
      testUser.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
      userRepository.save(testUser);

      testCategory = new Category();
      testCategory.setName("Category00");
      categoryRepository.save(testCategory);

      Ticket testTicket = new Ticket();
      testTicket.setSummary("this is test");
      testTicket.setDescription("test");
      testTicket.setStatus(TicketStatus.Open);
      testTicket.setPriority(TicketPriority.Medium);
      testTicket.setCreator(testUser);
      testTicket.setCategory(testCategory);
      testTicket.setOpenDate(new Timestamp(System.currentTimeMillis()));
      ticketRepository.save(testTicket);

      Ticket testTicket2 = new Ticket();
      testTicket2.setSummary("this is test");
      testTicket2.setDescription("test");
      testTicket2.setStatus(TicketStatus.Open);
      testTicket2.setPriority(TicketPriority.Low);
      testTicket2.setCreator(testUser);
      testTicket2.setCategory(testCategory);
      testTicket2.setOpenDate(new Timestamp(System.currentTimeMillis()));
      ticketRepository.save(testTicket2);
   }

   @Test
   void searchBySummaryOrDescription() {

      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
         .setSummaryOrDescription("test")
         .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);
      assertEquals("mik", response.getTicketInfo(0).getCreator());
   }

   @Test
   void searchByCategory() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
              .addCategory("Category00")
              .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);
      assertEquals(2, response.getTicketInfoCount());
   }

   @Test
   void searchByCreator() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
              .setCreator("mik")
              .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);
      assertEquals(2, response.getTicketInfoCount());
   }

   @Test
   void searchByTwoCriterias() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
         .addPriority(Msg.TicketPriority.Medium)
         .addStatus(Msg.TicketStatus.Open)
         .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);
      assertEquals("mik", response.getTicketInfo(0).getCreator());
   }

   @Test
   void searchNonExistingTicket() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
         .setCreator("this-user-doesn't-exist")
         .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);
      assertEquals(0, response.getTicketInfoCount());
   }

   @AfterEach
   void clearTestData() {
      ticketRepository.deleteAll();
      categoryRepository.delete(testCategory);
      userRepository.delete(testUser);
   }
}