package classes.beans;

import classes.entities.*;
import classes.entities.Comment;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.interfaces.ITicketManager;
import classes.repositories.CategoryRepository;
import classes.repositories.CommentRepository;
import classes.repositories.TicketRepository;
import classes.repositories.UserRepository;
import classes.util.ResponseHandler;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.Long.valueOf;
import static ticktrack.proto.Msg.*;

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
    private Logger logger = LoggerFactory.getLogger(User.class);

   @Autowired
   public TicketManager(TicketRepository ticketRepository, UserRepository userRepository, CommentRepository commentRepository, CategoryRepository categoryRepository) {
      this.ticketRepository = ticketRepository;
      this.userRepository = userRepository;
      this.commentRepository = commentRepository;
      this.categoryRepository = categoryRepository;
   }

   @Transactional
    @Override
    public CommonResponse create(TicketOp.TicketOpCreateRequest request) {
       String responseText;
       CommonResponse response;
       if(request != null) {
          TicketPriority priority;
          Optional<Category> categoryResult = categoryRepository.findByName(request.getCategory());
          if (categoryResult.isPresent()) {
             Category category = categoryResult.get();
             try {
                priority = TicketPriority.valueOf(request.getPriority());
                Ticket newTicket = new Ticket(request.getSummary(),
                   request.getDescription(),
                   priority,
                   category);
                newTicket.setStatus(TicketStatus.Open);
                responseText = "Ticket " + newTicket.getID() + " created!";
                logger.debug(responseText);
                response = CommonResponse.newBuilder()
                   .setResponseText(responseText)
                   .setResponseType(CommonResponse.ResponseType.Success)
                   .build();
             } catch (IllegalArgumentException e) {
                responseText = "Priority doesn't match with existing types!";
                logger.warn(responseText);
                response = CommonResponse.newBuilder()
                   .setResponseText(responseText)
                   .setResponseType(CommonResponse.ResponseType.Failure)
                   .build();
             }
          } else {
             responseText = "Request to create a Ticket is null";
             logger.warn(responseText);
             response = CommonResponse.newBuilder()
                .setResponseText(responseText)
                .setResponseType(CommonResponse.ResponseType.Failure)
                .build();
          }
       }else{
          responseText = "Category result is not present to create a ticket!";
          logger.debug(responseText);

          response = CommonResponse.newBuilder()
             .setResponseText(responseText)
             .setResponseType(CommonResponse.ResponseType.Failure)
             .build();
       }
       return response;
    }

    @Transactional
    @Override
    public CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request) {
        String responseText;
        CommonResponse response;
        Optional<Ticket> result = ticketRepository.findById(request.getTicketID());
        if(result.isPresent()) {
           Ticket ticket = result.get();
           if(request.hasAssignee()){
                  Optional<User> userResult = userRepository.findByUsername(request.getAssignee());
                  if (userResult.isPresent()) {
                     User assignee = userResult.get();
                     ticket.setAssignee(assignee);
                     ticket.setStatus(TicketStatus.Assigned);
                     ticketRepository.save(ticket);
                     responseText = "Ticket " + request.getTicketID() + "'s Assignee updated!";
                     logger.debug(responseText);

                     response = CommonResponse.newBuilder()
                        .setResponseText(responseText)
                        .setResponseType(CommonResponse.ResponseType.Success)
                        .build();
                  } else {
                     responseText = "There is no such a user to update assignee of ticket " + ticket.getID();
                     logger.debug(responseText);

                     response = CommonResponse.newBuilder()
                        .setResponseText(responseText)
                        .setResponseType(CommonResponse.ResponseType.Failure)
                        .build();
                  }
            }else if(request.hasDescription()){
               ticket.setDescription(request.getDescription());
               responseText = "Ticket " + request.getTicketID() + "'s Description updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
            }else if(request.hasCategory()){
              Optional<Category> categoryResult = categoryRepository.findByName(request.getCategory());
              if(categoryResult.isPresent()) {
                 Category category = categoryResult.get();
                 ticket.setCategory(category);
                 responseText = "Ticket " + request.getTicketID() + "'s Category updated!";
                 logger.debug(responseText);

                 response = CommonResponse.newBuilder()
                    .setResponseText(responseText)
                    .setResponseType(CommonResponse.ResponseType.Success)
                    .build();
              }else {
                 responseText = "There is no such a user to update assignee of ticket " + ticket.getID();
                 logger.debug(responseText);

                 response = CommonResponse.newBuilder()
                    .setResponseText(responseText)
                    .setResponseType(CommonResponse.ResponseType.Failure)
                    .build();
              }
            }else if(request.hasStatus()){
               TicketStatus status;
               try {
                  status = TicketStatus.valueOf(request.getStatus().toString());
                  ticket.setStatus(status);
                  responseText = "Ticket " + request.getTicketID() + "' Status updated!";
                  logger.debug(responseText);

                  response = CommonResponse.newBuilder()
                     .setResponseText(responseText)
                     .setResponseType(CommonResponse.ResponseType.Success)
                     .build();
               } catch (IllegalArgumentException e) {
                  responseText = "Status doesn't match with existing types!";
                  logger.warn(responseText);
                  response = CommonResponse.newBuilder()
                     .setResponseText(responseText)
                     .setResponseType(CommonResponse.ResponseType.Failure)
                     .build();
               }
            }else if(request.hasSummary()){
               ticket.setSummary(request.getSummary());
               responseText = "Ticket " + request.getTicketID() + "'s Summary updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
            }else if (request.hasDeadline()){
               ticket.setDeadline(new Timestamp(request.getDeadline()));
               responseText = "Ticket " + request.getTicketID() + "'s Deadline updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
            }else if(request.hasPriority()){
               TicketPriority priority;
               try {
                  priority = TicketPriority.valueOf(request.getPriority().toString());
                  ticket.setPriority(priority);
                  responseText = "Ticket " + request.getTicketID() + "' priority updated!";
                  logger.debug(responseText);

                  response = CommonResponse.newBuilder()
                     .setResponseText(responseText)
                     .setResponseType(CommonResponse.ResponseType.Success)
                     .build();
               } catch (IllegalArgumentException e) {
                  responseText = "Priority doesn't match with existing types!";
                  logger.warn(responseText);
                  response = CommonResponse.newBuilder()
                     .setResponseText(responseText)
                     .setResponseType(CommonResponse.ResponseType.Failure)
                     .build();
               }
            }else if(request.hasResolution()){
               ticket.setResolution(request.getResolution());
               responseText = "Ticket " + request.getTicketID() + "'s Resolution updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
            }else{
               responseText = "Nothing updated in ticket " + request.getTicketID();
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
            }
        }else{
           responseText = "There is no ticket by ID " + request.getTicketID();
           logger.warn(responseText);
           response = CommonResponse.newBuilder()
              .setResponseText(responseText)
              .setResponseType(CommonResponse.ResponseType.Failure)
              .build();
        }

        return response;
    }

    @Transactional
    @Override
    public CommonResponse addComment(TicketOp.TicketOpAddComment request) {
        String responseText;
        CommonResponse response;
        Optional<Ticket> result = ticketRepository.findById(request.getTicketId());
        if(result.isPresent()) {
              Comment comment = new Comment(request.getNewComment().getUsername(),
                 new Timestamp(request.getNewComment().getTime()),
                 request.getNewComment().getText());
              result.get().setComment(comment);
              comment.setTicket(result.get());
              commentRepository.save(comment);

              responseText = "User " + request.getNewComment().getUsername() + " added comment "
                 + request.getNewComment().getText() + " at " + request.getNewComment().getTime();
              logger.debug(responseText);

              response = CommonResponse.newBuilder()
                 .setResponseText(responseText)
                 .setResponseType(CommonResponse.ResponseType.Success)
                 .build();
        }else {
           responseText = "Ticket " + request.getTicketId() + " not found!";
           logger.warn(responseText);
           response = CommonResponse.newBuilder()
              .setResponseText(responseText)
              .setResponseType(CommonResponse.ResponseType.Failure)
              .build();
        }
        return response;
    }

    @Transactional
    @Override
    public TicketOp.TicketOpGetResponse get(long ticket_id) {
       Optional<Ticket> result = ticketRepository.findById(ticket_id);
       TicketOp.TicketOpGetResponse response;
        if (result.isPresent()) {
            logger.debug("Query for {} ticket received", ticket_id);
           // response = ticktrack.proto.TicketInfo.newBuilder()
               //.setStatus(result.get().getStatus().toString())
              // .build();
            return null; //response; //todo
        } else {
            logger.debug("Ticket {} not found", ticket_id);
            return null;
        }
    }

    @Transactional
    @Override
    public SearchOp.SearchOpResponse getAll() {
       return ResponseHandler.composeResponseMessageFromQueryResult(Streams.stream(ticketRepository.findAll()).collect(Collectors.toList()));
    }

}
