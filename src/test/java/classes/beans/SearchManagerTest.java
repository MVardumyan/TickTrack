package classes.beans;

import classes.TickTrackContext;
import classes.entities.Category;
import classes.entities.Ticket;
import classes.entities.User;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.enums.UserRole;
import classes.repositories.CategoryRepository;
import classes.repositories.TicketRepository;
import classes.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.proto.Msg;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TickTrackContext.class)
class SearchManagerTest {
    private static SearchManager searchManager;
    private static TicketRepository ticketRepository;
    private static UserRepository userRepository;
    private static CategoryRepository categoryRepository;
    private static User testUser;
    private static Ticket testTicket;
    private static Category testCategory;
    private static long testOpenDate;

    @BeforeAll
    static void initManager() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
        searchManager = (SearchManager) context.getBean("SearchMng");
        ticketRepository = context.getBean(TicketRepository.class);
        userRepository = context.getBean(UserRepository.class);
        categoryRepository = context.getBean(CategoryRepository.class);

        testUser = new User();
        testUser.setUsername("mik");
        testUser.setActiveStatus(true);
        testUser.setFirstName("mikayel");
        testUser.setLastName("vardumyan");
        testUser.setEmail("mikayel2505@gmail.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.BusinessUser);
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

    @AfterAll
    static void clearTestData() {
        ticketRepository.delete(testTicket);
        categoryRepository.delete(testCategory);
        userRepository.delete(testUser);
    }
}