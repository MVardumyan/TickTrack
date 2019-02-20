package classes.beans;

import classes.TickTrackContext;
import classes.entities.Ticket;
import classes.entities.User;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.enums.UserRole;
import classes.repositories.TicketRepository;
import classes.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
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
class SearchManagerTest {
    private static SearchManager searchManager;
    private static TicketRepository ticketRepository;
    private static UserRepository userRepository;
    private static User testUser;
    private static Ticket testTicket;

    @BeforeAll
    static void initManager() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TickTrackContext.class);
        searchManager = (SearchManager) context.getBean("SearchMng");
        ticketRepository = context.getBean(TicketRepository.class);
        userRepository = context.getBean(UserRepository.class);

        testUser = new User();
        testUser.setUsername("mik");
        testUser.setActiveStatus(true);
        testUser.setFirstName("mikayel");
        testUser.setLastName("Vardumyan");
        testUser.setEmail("mikayel2505@gmail.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.BusinessUser);
        userRepository.save(testUser);

        testTicket = new Ticket();
        testTicket.setSummary("this is test");
        testTicket.setDescription("test");
        testTicket.setStatus(TicketStatus.Open);
        testTicket.setPriority(TicketPriority.Medium);
        testTicket.setCreator(testUser);
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

    @AfterAll
    static void clearTestData() {
        ticketRepository.delete(testTicket);
        userRepository.delete(testUser);
    }
}