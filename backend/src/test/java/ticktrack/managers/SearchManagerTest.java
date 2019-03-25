package ticktrack.managers;

import org.joda.time.DateTime;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
      testTicket2.setSummary("this is test 2");
      testTicket2.setDescription("test");
      testTicket2.setStatus(TicketStatus.Open);
      testTicket2.setPriority(TicketPriority.Low);
      testTicket2.setCreator(testUser);
      testTicket2.setCategory(testCategory);
      testTicket2.setOpenDate(new Timestamp(System.currentTimeMillis()));
      testTicket2.setDeadline(new Timestamp(DateTime.now().plusDays(5).getMillis()));
      testTicket2.setResolution("resolved");
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
   void searchByResolution() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
              .setResolution("res")
              .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);

      assertEquals(1, response.getTicketInfoCount());
      assertEquals("this is test 2", response.getTicketInfo(0).getSummary());
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
   void searchByOpenDateRange() {
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate dateEnd = LocalDate.now();
      LocalDate dateStart = dateEnd.minusDays(1);

      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
              .setOpenDateStart(dateFormat.format(dateStart))
              .setOpenDateEnd(dateFormat.format(dateEnd))
              .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);
      assertEquals(2, response.getTicketInfoCount());
   }

   @Test
   void searchByDeadline() {
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate deadlineStart = LocalDate.now();
      LocalDate deadlineEnd = deadlineStart.plusDays(10);

      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
              .setDeadlineStart(dateFormat.format(deadlineStart))
              .setDeadlineEnd(dateFormat.format(deadlineEnd))
              .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);
      assertEquals(1, response.getTicketInfoCount());
   }

   @Test
   void searchNonExistingTicket() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
         .setCreator("this-user-doesn't-exist")
         .build();

      Msg.SearchOp.SearchOpResponse response = searchManager.searchByCriteria(request,1,10);
      assertEquals(0, response.getTicketInfoCount());
   }

   @Test
   void searchByInvalidCategory() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
              .addCategory("non-existing")
              .build();

      Msg.SearchOp.SearchOpResponse searchOpResponse = searchManager.searchByCriteria(request, 1, 10);

      assertEquals(0, searchOpResponse.getTicketInfoCount());
   }

   @Test
   void searchByInvalidAssignee() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
              .setAssignee("someone")
              .addStatus(Msg.TicketStatus.Open)
              .build();

      Msg.SearchOp.SearchOpResponse searchOpResponse = searchManager.searchByCriteria(request, 1, 10);

      assertEquals(0, searchOpResponse.getTicketInfoCount());
   }

   @Test
   void searchByInvalidCreator() {
      Msg.SearchOp.SearchOpRequest request = Msg.SearchOp.SearchOpRequest.newBuilder()
              .setCreator("someone")
              .build();

      Msg.SearchOp.SearchOpResponse searchOpResponse = searchManager.searchByCriteria(request, 1, 10);

      assertEquals(0, searchOpResponse.getTicketInfoCount());
   }

   @Test
   void searchUser() {
      List<String> result = searchManager.searchUsersByTerm("i");

      assertEquals(1, result.size());
      assertEquals("mik", result.get(0));
   }

   @AfterEach
   void clearTestData() {
      ticketRepository.deleteAll();
      categoryRepository.delete(testCategory);
      userRepository.delete(testUser);
   }
}