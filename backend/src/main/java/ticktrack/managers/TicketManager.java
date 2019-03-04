package ticktrack.managers;

import ticktrack.entities.*;
import ticktrack.entities.Comment;
import ticktrack.enums.TicketPriority;
import ticktrack.enums.TicketStatus;
import ticktrack.interfaces.ITicketManager;
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

@Service("ticketMng")
public class TicketManager implements ITicketManager {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private Logger logger = LoggerFactory.getLogger(User.class);

    @Autowired
    public TicketManager(TicketRepository ticketRepository, UserRepository userRepository, CommentRepository commentRepository, CategoryRepository categoryRepository, GroupRepository groupRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    @Override
    public CommonResponse create(TicketOp.TicketOpCreateRequest request) {
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
                    return buildFailureResponse(responseText);
                }

                Ticket newTicket = new Ticket(request.getSummary(),
                        request.getDescription(),
                        priority,
                        creator,
                        category);
                newTicket.setStatus(Open);
                newTicket.setOpenDate(new Timestamp(System.currentTimeMillis()));

                if (request.hasDeadline()) {
                    newTicket.setDeadline(new Timestamp(request.getDeadline()));
                }

                if (request.hasAssignee()) {
                    userRepository.findById(request.getAssignee())
                            .ifPresent(newTicket::setAssignee);
                } else if (request.hasGroup()) {
                    groupRepository.findByName(request.getGroup())
                            .ifPresent(newTicket::setGroup);
                }

                ticketRepository.save(newTicket);

                responseText = "Ticket " + newTicket.getID() + " created!";
                logger.debug(responseText);
                response = buildSuccessResponse(responseText);

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
        return response;
    }

    @Transactional
    @Override
    public CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request) {
        StringBuilder responseText = new StringBuilder();
        CommonResponse response;
        boolean updateSuccess = false;

        Optional<Ticket> result = ticketRepository.findById(request.getTicketID());
        if (result.isPresent()) {
            Ticket ticket = result.get();

            if (request.hasAssignee()) {
                Optional<User> userResult = userRepository.findByUsername(request.getAssignee());
                if (userResult.isPresent()) {
                    User assignee = userResult.get();
                    ticket.setAssignee(assignee);
                    if(Open.equals(ticket.getStatus())) {
                        ticket.setStatus(Assigned);
                    }

                    responseText.append("Ticket ").append(request.getTicketID()).append("'s Assignee updated!\n");
                    updateSuccess = true;
                } else {
                    responseText.append("There is no such a user to update assignee of ticket ").append(ticket.getID()).append("\n");
                }
            }
            if (request.hasDescription()) {
                ticket.setDescription(request.getDescription());
                responseText.append("Ticket ").append(request.getTicketID()).append("'s Description updated!\n");
                updateSuccess = true;
            }
            if (request.hasCategory()) {
                Optional<Category> categoryResult = categoryRepository.findByName(request.getCategory());
                if (categoryResult.isPresent()) {
                    Category category = categoryResult.get();
                    ticket.setCategory(category);
                    responseText.append("Ticket ").append(request.getTicketID()).append("'s Category updated!\n");
                    updateSuccess = true;
                } else {
                    responseText.append("There is no such category to update ticket ").append(ticket.getID()).append("\n");
                }
            }
            if (request.hasStatus()) {
                TicketStatus status;
                try {
                    status = valueOf(request.getStatus().toString());
                    ticket.setStatus(status);
                    if(status.equals(Closed) || status.equals(Canceled)) {
                        ticket.setCloseDate(new Timestamp(System.currentTimeMillis()));
                    }
                    responseText.append("Ticket ").append(request.getTicketID()).append("' Status updated!\n");
                    updateSuccess = true;
                } catch (IllegalArgumentException e) {
                    responseText.append("Status doesn't match with existing types!\n");
                }
            }
            if (request.hasSummary()) {
                ticket.setSummary(request.getSummary());
                responseText.append("Ticket ").append(request.getTicketID()).append("'s Summary updated!\n");
                updateSuccess = true;
            }
            if (request.hasDeadline()) {
                ticket.setDeadline(new Timestamp(request.getDeadline()));
                responseText.append("Ticket ").append(request.getTicketID()).append("'s Deadline updated!\n");
                updateSuccess = true;
            }
            if (request.hasPriority()) {
                TicketPriority priority;
                try {
                    priority = TicketPriority.valueOf(request.getPriority().toString());
                    ticket.setPriority(priority);
                    responseText.append("Ticket ").append(request.getTicketID()).append("' priority updated!\n");
                    updateSuccess = true;
                } catch (IllegalArgumentException e) {
                    responseText.append("Priority doesn't match with existing types!\n");
                }
            }
            if (request.hasResolution()) {
                ticket.setResolution(request.getResolution());
                responseText.append("Ticket ").append(request.getTicketID()).append("'s Resolution updated!\n");
                updateSuccess = true;
            } else {
                responseText.append("Nothing updated in ticket ").append(request.getTicketID()).append("\n");
            }
            if (request.hasGroup()) {
                Optional<UserGroup> groupResult = groupRepository.findByName(request.getGroup());
                if(groupResult.isPresent()) {
                    UserGroup group = groupResult.get();
                    ticket.setGroup(group);

                    if(Open.equals(ticket.getStatus())) {
                        ticket.setStatus(Assigned);
                    }
                    if(ticket.getAssignee() != null && !ticket.getAssignee().getGroup().equals(group)) {
                        ticket.setAssignee(null);
                    }

                    responseText.append("Ticket ").append(request.getTicketID()).append(" assigned to group ").append(group.getName());
                    updateSuccess = true;
                } else {
                    responseText.append("Group ").append(request.getGroup()).append(" not found");
                }
            }
            if(updateSuccess) {
                ticketRepository.save(ticket);
                logger.debug(responseText.toString());
                response = buildSuccessResponse(responseText.toString());
            } else {
                logger.warn(responseText.toString());
                response = buildFailureResponse(responseText.toString());
            }

        } else {
            responseText.append("There is no ticket by ID ").append(request.getTicketID());
            logger.warn(responseText.toString());
            response = buildFailureResponse(responseText.toString());
        }

        return response;
    }

    @Transactional
    @Override
    public CommonResponse addComment(TicketOp.TicketOpAddComment request) {
        String responseText;
        CommonResponse response;
        Optional<Ticket> result = ticketRepository.findById(request.getTicketId());
        if (result.isPresent()) {
            Comment comment = new Comment(request.getNewComment().getUsername(),
                    new Timestamp(request.getNewComment().getTime()),
                    request.getNewComment().getText());

            comment.setTicket(result.get());
            commentRepository.save(comment);

            responseText = "User " + request.getNewComment().getUsername() + " added comment "
                    + request.getNewComment().getText() + " at " + request.getNewComment().getTime();
            logger.debug(responseText);

            response = buildSuccessResponse(responseText);
        } else {
            responseText = "Ticket " + request.getTicketId() + " not found!";
            logger.warn(responseText);
            response = buildFailureResponse(responseText);
        }
        return response;
    }

    @Transactional
    @Override
    public TicketInfo get(long ticket_id) {
        Optional<Ticket> result = ticketRepository.findById(ticket_id);
        if (result.isPresent()) {
            logger.debug("Query for {} ticket received", ticket_id);
            return buildTicketInfo(result.get());
        } else {
            logger.debug("Ticket {} not found", ticket_id);
            return null;
        }
    }

    @Transactional
    @Override
    public SearchOp.SearchOpResponse getAll() {
        return buildTicketResponseFromQueryResult(Streams.stream(ticketRepository.findAll()).collect(Collectors.toList()));
    }

}
