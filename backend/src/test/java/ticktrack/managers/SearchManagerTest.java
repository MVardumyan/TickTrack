package ticktrack.managers;

import org.junit.jupiter.api.*;
import ticktrack.entities.Category;
import ticktrack.entities.Ticket;
import ticktrack.entities.User;
import ticktrack.enums.Gender;
import ticktrack.enums.TicketPriority;
import ticktrack.enums.TicketStatus;
import ticktrack.enums.UserRole;
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
   private Ticket testTicket;
   private Category testCategory;
   private long testOpenDate;

   @BeforeEach
   void initTestData() {
      testUser = new User();
      testUser.setUsername("mik");
      testUser.setActiveStatus(true);
      testUser.setFirstName("mikayel");
      testUser.setLastName("vardumyan");
      testUser.setEmail("mikayel2505@gmail.com");
      testUser.setPassword("password");
      testUser.setRole(UserRole.BusinessUser);
      testUser.setGender(Gender.Male);
      testUser.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
      userRepository.save(testUser);

      testCategory = new Category();
      testCategory.setName("Category00");
      categoryRepository.save(testCategory);

      testTicket = new Ticket();
      testTicket.setSummary("this is test");
      testTicket.setDescription("test");
      testTicket.setStatus(TicketStatus.Open);
      testTicket.setPriority(TicketPriority.Medium);
      testTicket.setCreator(testUser);
      testTicket.setCategory(testCategory);
      testOpenDate = System.currentTimeMillis();
      testTicket.setOpenDate(new Timestamp(testOpenDate));
      ticketRepository.save(testTicket);
   }

   @Test
   void searchBySummaryOrDescription() {

      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
         .setSummaryOrDescription("test")
         .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request);
      assertEquals("mik", response.getTicketInfo(0).getCreator());
   }

   @Test
   void searchByOpenDate() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
         .setOpenDateStart(testOpenDate)
         .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request);
      assertEquals("mik", response.getTicketInfo(0).getCreator());
   }

   @Test
   void searchByTwoCriterias() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
         .addPriority(Msg.TicketPriority.Medium)
         .addStatus(Msg.TicketStatus.Open)
         .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request);
      assertEquals("mik", response.getTicketInfo(0).getCreator());
   }

   @Test
   void searchNonExistingTicket() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
         .setCreator("this-user-doesn't-exist")
         .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request);
      assertEquals(0, response.getTicketInfoCount());
   }

   @AfterEach
   void clearTestData() {
      ticketRepository.delete(testTicket);
      categoryRepository.delete(testCategory);
      userRepository.delete(testUser);
   }
}