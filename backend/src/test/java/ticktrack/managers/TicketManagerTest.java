package ticktrack.managers;

import common.enums.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ticktrack.entities.*;
import ticktrack.enums.Gender;
import ticktrack.enums.TicketPriority;
import ticktrack.enums.TicketStatus;
import ticktrack.proto.Msg;
import ticktrack.repositories.*;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ticktrack.enums.TicketPriority.High;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TicketManagerTest {

    @Autowired
    TicketManager ticketManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    TicketRepository ticketRepository;
    private User testUser;
    private Category testCategory;
    private Msg.Comment testComment;

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

        testComment = Msg.Comment.newBuilder()
        .setText("test comment")
        .setUsername("John")
        .setTime("000000")
                .build();
    }

    @Test
    void createTicket() {
       Msg.TicketOp.TicketOpCreateRequest request = Msg.TicketOp.TicketOpCreateRequest.newBuilder()
               .setPriority("High")
               .setSummary("Summary")
               .setDescription("Desc")
               .setCategory(testCategory.getName())
               .setCreator(testUser.getUsername())
               .build();

        assertEquals("High", request.getPriority());
        assertEquals("Summary",request.getSummary());
        assertEquals("Desc",request.getDescription());
        assertEquals(testCategory.getName(),request.getCategory());
    }

    @Test
    void updateTicket(){
        Msg.TicketOp.TicketOpUpdateRequest request = Msg.TicketOp.TicketOpUpdateRequest.newBuilder()
                .setTicketID(0)
                .setStatus(Msg.TicketStatus.InProgress)
                .setAssignee(testUser.getUsername())
                .setResolution("resolution")
                .setPriority(Msg.TicketPriority.High)
                .setCategory(testCategory.getName())
                .setDescription("desc")
                .setSummary("summary")
                .build();

        assertEquals(testUser.getUsername(),request.getAssignee());
        assertEquals(Msg.TicketStatus.InProgress,request.getStatus());
        assertEquals(0,request.getTicketID());
        assertEquals(testUser.getUsername(),request.getAssignee());
        assertEquals("resolution",request.getResolution());
        assertEquals(Msg.TicketPriority.High,request.getPriority());
        assertEquals("desc",request.getDescription());
        assertEquals(testCategory.getName(),request.getCategory());
        assertEquals("summary",request.getSummary());
    }

    @Test
    void addComment(){
        Msg.TicketOp.TicketOpAddComment request = Msg.TicketOp.TicketOpAddComment.newBuilder()
                .setNewComment(testComment)
                .setTicketId(0)
                .build();

        assertEquals(testComment,request.getNewComment());

        Msg.TicketOp.TicketOpAddComment falseRequest = Msg.TicketOp.TicketOpAddComment.newBuilder()
                .setNewComment(testComment)
                .setTicketId(1)
                .build();
        Optional<Ticket> result = ticketRepository.findById(falseRequest.getTicketId());
        if(!result.isPresent()) {
            assertTrue(true);
        }
    }

    @Test
    void get(){
        Optional<Ticket> result = ticketRepository.findById((long)0);
        if(result.isPresent()){
            assertTrue(true);
        }
        Optional<Ticket> falseResult = ticketRepository.findById((long)1);
        if(result.isPresent()){
            assertTrue(false);
        }
    }

    @AfterEach
    void clearTestData() {
        commentRepository.deleteAll();
        ticketRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }
}