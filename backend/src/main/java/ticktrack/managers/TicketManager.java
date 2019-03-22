package ticktrack.managers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import ticktrack.entities.*;
import ticktrack.entities.Comment;
import ticktrack.enums.TicketPriority;
import ticktrack.enums.TicketStatus;
import ticktrack.interfaces.ITicketManager;
import ticktrack.proto.Msg;
import ticktrack.repositories.*;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ticktrack.enums.TicketStatus.*;
import static ticktrack.proto.Msg.*;
import static ticktrack.util.ResponseHandler.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class provides methods for managing Ticket entity.
 * TicketManager is Spring component. For db interaction it uses autowired crudRepository interfaces.
 * Contains business logic for new Ticket creation, update and adding Comments.
 */
@Service("ticketMng")
public class TicketManager implements ITicketManager {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private Logger logger = LoggerFactory.getLogger(TicketManager.class);

    @Autowired
    public TicketManager(TicketRepository ticketRepository, UserRepository userRepository, CommentRepository commentRepository, CategoryRepository categoryRepository, GroupRepository groupRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Method for new ticket creation.
     * Sets ticket ID, creation date and status automatically
     * @param request protobuf type TicketOpCreateRequest contains new Ticket fields information
     * @return TicketInfo - new ticket's information for success; CommonResponse - if Creator/Priority/Category not found
     */
    @Transactional
    @Override
    public Msg create(TicketOp.TicketOpCreateRequest request) {
        String responseText;
        CommonResponse response;
        if (request == null) {
            responseText = "Request is empty, unable to create a ticket!";
            logger.debug(responseText);

            response = buildFailureResponse(responseText);
        } else {
            User creator;
            TicketPriority priority;
            Optional<Category> categoryResult = categoryRepository.findByName(request.getCategory());
            Optional<User> creatorResult = userRepository.findById(request.getCreator());
            if (categoryResult.isPresent() && creatorResult.isPresent() && request.hasCreator()) {
                Category category = categoryResult.get();
                try {
                    creator = creatorResult.get();
                    priority = TicketPriority.valueOf(request.getPriority());
                } catch (IllegalArgumentException e) {
                    responseText = "Priority doesn't match with existing types OR there is no such a user to assign to this ticket!";
                    logger.warn(responseText);
                    return wrapCommonResponseIntoMsg(buildFailureResponse(responseText));
                }

                Ticket newTicket = new Ticket(request.getSummary(),
                        request.getDescription(),
                        priority,
                        creator,
                        category);
                newTicket.setStatus(Open);
                newTicket.setOpenDate(new Timestamp(getCurrentTimeInMillis()));

                if (request.hasDeadline()) {
                    DateTime deadline = new DateTime(request.getDeadline());
                    newTicket.setDeadline(new Timestamp(deadline.getMillis()));
                }

                if (request.hasAssignee()) {
                    userRepository.findByUsername(request.getAssignee())
                            .ifPresent(newTicket::setAssignee);
                    newTicket.setStatus(Assigned);
                } else if (request.hasGroup()) {
                    groupRepository.findByName(request.getGroup())
                            .ifPresent(newTicket::setGroup);
                }

                ticketRepository.save(newTicket);

                responseText = "Ticket " + newTicket.getID() + " created!";
                logger.debug(responseText);
                return wrapIntoMsg(buildTicketInfo(newTicket));

            } else if (!categoryResult.isPresent()) {
                responseText = "Unable to create Ticket: Invalid Category type";
                logger.warn(responseText);
                response = buildFailureResponse(responseText);
            } else {
                responseText = "Unable to create Ticket: Creator does not exist";
                logger.warn(responseText);
                response = buildFailureResponse(responseText);
            }
        }
        return wrapCommonResponseIntoMsg(response);
    }

    /**
     * Method for ticket fields update.
     * Checks which fields should be updated and updates them with new values.
     * @param request TicketOpUpdateRequest contains set of optional fields to update
     * @return TicketInfo - new ticket's information for success; CommonResponse/ - if ticket_id/Status/Creator/Priority/Category not found
     */
    @Transactional
    @Override
    public Msg updateTicket(TicketOp.TicketOpUpdateRequest request) {
        StringBuilder responseText = new StringBuilder();
        CommonResponse response;
        boolean updateSuccess = false;

        Optional<Ticket> result = ticketRepository.findById(request.getTicketID());
        if (result.isPresent()) {
            Ticket ticket = result.get();

            if (request.hasGroup()) {
                Optional<UserGroup> groupResult = groupRepository.findByName(request.getGroup());
                if (groupResult.isPresent()) {
                    UserGroup group = groupResult.get();
                    ticket.setGroup(group);

                    if (Open.equals(ticket.getStatus())) {
                        ticket.setStatus(Assigned);
                    }
                    if (ticket.getAssignee() != null) {
                        ticket.setAssignee(null);
                    }

                    responseText.append("Ticket ").append(request.getTicketID()).append(" assigned to group ").append(group.getName()).append(" ");
                    updateSuccess = true;
                } else {
                    responseText.append("Group ").append(request.getGroup()).append(" not found ");
                }
            }
            if (request.hasAssignee()) {
                Optional<User> userResult = userRepository.findByUsername(request.getAssignee());
                if (userResult.isPresent()) {
                    User assignee = userResult.get();
                    ticket.setAssignee(assignee);
                    if (Open.equals(ticket.getStatus())) {
                        ticket.setStatus(Assigned);
                    }
                    if(ticket.getGroup() != null) {
                        ticket.setGroup(null);
                    }

                    responseText.append("Ticket ").append(request.getTicketID()).append("'s Assignee updated! ");
                    updateSuccess = true;
                } else {
                    responseText.append("There is no such a user to update assignee of ticket ").append(ticket.getID()).append(" ");
                }
            }
            if (request.hasDescription()) {
                ticket.setDescription(request.getDescription());
                responseText.append("Ticket ").append(request.getTicketID()).append("'s Description updated! ");
                updateSuccess = true;
            }
            if (request.hasCategory()) {
                Optional<Category> categoryResult = categoryRepository.findByName(request.getCategory());
                if (categoryResult.isPresent()) {
                    Category category = categoryResult.get();
                    ticket.setCategory(category);
                    responseText.append("Ticket ").append(request.getTicketID()).append("'s Category updated! ");
                    updateSuccess = true;
                } else {
                    responseText.append("There is no such category to update ticket ").append(ticket.getID()).append(" ");
                }
            }
            if (request.hasStatus()) {
                TicketStatus status;
                try {
                    status = valueOf(request.getStatus().toString());
                    ticket.setStatus(status);
                    if (status.equals(Closed) || status.equals(Canceled)) {
                        ticket.setCloseDate(new Timestamp(getCurrentTimeInMillis()));
                    } else if(status.equals(Resolved)) {
                        ticket.setAssignee(ticket.getCreator());
                    }
                    responseText.append("Ticket ").append(request.getTicketID()).append("' Status updated! ");
                    updateSuccess = true;
                } catch (IllegalArgumentException e) {
                    responseText.append("Status doesn't match with existing types!\n");
                }
            }
            if (request.hasSummary()) {
                ticket.setSummary(request.getSummary());
                responseText.append("Ticket ").append(request.getTicketID()).append("'s Summary updated! ");
                updateSuccess = true;
            }
            if (request.hasDeadline()) {
                DateTime deadline = new DateTime(request.getDeadline());
                ticket.setDeadline(new Timestamp(deadline.getMillis()));

                responseText.append("Ticket ").append(request.getTicketID()).append("'s Deadline updated! ");
                updateSuccess = true;
            }
            if (request.hasPriority()) {
                TicketPriority priority;
                try {
                    priority = TicketPriority.valueOf(request.getPriority().toString());
                    ticket.setPriority(priority);
                    responseText.append("Ticket ").append(request.getTicketID()).append("' priority updated! ");
                    updateSuccess = true;
                } catch (IllegalArgumentException e) {
                    responseText.append("Priority doesn't match with existing types! ");
                }
            }
            if (request.hasResolution()) {
                ticket.setResolution(request.getResolution());
                responseText.append("Ticket ").append(request.getTicketID()).append("'s Resolution updated! ");
                updateSuccess = true;
            } else {
                responseText.append("Nothing updated in ticket ").append(request.getTicketID()).append(" ");
            }
            if (updateSuccess) {
                ticketRepository.save(ticket);
                logger.debug(responseText.toString());
                return wrapIntoMsg(buildTicketInfo(ticket));
            } else {
                logger.warn(responseText.toString());
                response = buildFailureResponse(responseText.toString());
            }

        } else {
            responseText.append("There is no ticket by ID ").append(request.getTicketID());
            logger.warn(responseText.toString());
            response = buildFailureResponse(responseText.toString());
        }

        return wrapCommonResponseIntoMsg(response);
    }

    /**
     * Method creates new Comment entity and adds it to corresponding ticket
     * @param request protobuf type TicketOpAddComment contains new Comment information
     * @return protobuf type Comment with new Comment information in case of success. CommonResponse with failure message in case of failure
     */
    @Transactional
    @Override
    public Msg addComment(TicketOp.TicketOpAddComment request) {
        String responseText;
        CommonResponse response;
        Optional<Ticket> result = ticketRepository.findById(request.getTicketId());
        if (result.isPresent()) {
            Ticket ticket = result.get();
            Comment comment = new Comment(request.getNewComment().getUsername(),
                    new Timestamp(getCurrentTimeInMillis()),
                    request.getNewComment().getText());

            comment.setTicket(ticket);
            commentRepository.save(comment);

            responseText = "User " + request.getNewComment().getUsername() + " added comment "
                    + request.getNewComment().getText() + " at " + request.getNewComment().getTime();
            logger.debug(responseText);

            return wrapCommentIntoMsg(Msg.Comment.newBuilder()
                    .setUsername(comment.getUsername())
                    .setText(comment.getText())
                    .setTime(comment.getTimestamp().toString())
            );
        }
        responseText = "Ticket " + request.getTicketId() + " not found!";
        logger.warn(responseText);
        response = buildFailureResponse(responseText);
        return wrapCommonResponseIntoMsg(response);
    }

    /**
     * Method searches ticket in db by given id and returns its information
     * @param ticket_id param for search by id
     * @return protobuf type TicketInfo with ticket fields information. Empty if ticket not found
     */
    @Transactional
    @Override
    public TicketInfo get(long ticket_id) {
        Optional<Ticket> result = ticketRepository.findById(ticket_id);
        if (result.isPresent()) {
            logger.debug("Query for {} ticket received", ticket_id);
            return buildTicketInfo(result.get());
        } else {
            logger.debug("Ticket {} not found", ticket_id);
            return TicketInfo.newBuilder().build();
        }
    }

    @Transactional
    @Override
    public SearchOp.SearchOpResponse getAll() {
        return buildTicketResponseFromQueryResult(Streams.stream(ticketRepository.findAll()).collect(Collectors.toList()));
    }

    private long getCurrentTimeInMillis() {
        return DateTime.now().withZone(DateTimeZone.forID("Asia/Yerevan")).getMillis();
    }

    private Msg wrapCommentIntoMsg(Msg.Comment.Builder comment) {
        return Msg.newBuilder()
                .setComment(comment)
                .build();
    }

    private Msg wrapIntoMsg(TicketInfo ticketInfo) {
        return Msg.newBuilder()
                .setTicketInfo(ticketInfo)
                .build();
    }

}
