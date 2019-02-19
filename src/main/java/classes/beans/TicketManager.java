package classes.beans;

import classes.entities.*;
import classes.entities.Comment;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.interfaces.ITicketManager;
import classes.repositories.CommentRepository;
import classes.repositories.TicketRepository;
import com.google.common.collect.Streams;
import net.bytebuddy.build.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static ticktrack.proto.Msg.*;
import static ticktrack.proto.TicketOp.TicketOpUpdateRequest.ParameterName.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketManager implements ITicketManager {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private CommentRepository commentRepository;
    private Logger logger = LoggerFactory.getLogger(User.class);

    @Transactional //todo don't forget transactional
    @Override
    public CommonResponse create(TicketOp.TicketOpCreateRequest request) {
       String responseText;
       CommonResponse response;

       if(request != null){

          Ticket newTicket = new Ticket(request.getSummary(),request.getDescription(),
             request.getPriority(),request.getCategory()); //todo new function for comparison

          responseText = "Ticket " + newTicket.getID() + " created!";
          logger.debug(responseText);

          response = CommonResponse.newBuilder()
             .setResponseText(responseText)
             .setResponseType(CommonResponse.ResponseType.Success)
             .build();
       }else {
          responseText = "Request to create a Ticket is null";
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
    public CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request) {
        Optional<Ticket> result = ticketRepository.findByID(request.getTicketID());
        if(result.isPresent()) {
            Ticket ticket = result.get();
        switch(request.getParamName()) {
            case Summary:
                result.get().setSummary(String.valueOf(request.getValue()));
            case Status:
//                for (TicketStatus status : TicketStatus.values()) {
//                    if(status.equals(request.getValue())){
//                        result.get().setStatus(status);
//                        break;
//                    }
//                }
                ticket.setStatus(TicketStatus.valueOf(request.getValue()));
            case Assignee:
                result.get().setAssignee(request.getParamName());
            case Deadline:
                result.get().setDeadline(request.getParamName());
        } else {

            }
//todo
        }

        return null;
    }

    @Transactional
    @Override
    public CommonResponse addComment(TicketOp.TicketOpAddComment request) {
        String responseText;
        CommonResponse response;

        Optional<Ticket> result = ticketRepository.findByID(request.getTicketId());
        if(request != null) {
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
            responseText = " request is null ";
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
        return null;
    }

//    @Transactional
//    @Override
//    public TicketOp.TicketOpGetResponse get(TicketOp.TicketOpGetRequest request) {
//        Optional<Ticket> result = ticketRepository.findByID(request.getTicketId());
//        if (result.isPresent()) {
//            logger.debug("Query for {} ticket received", request.getTicketId());
//            return null; //todo
//        } else {
//            logger.debug("Ticket {} not found", request.getTicketId());
//            return null;
//        }
//    }



    @Transactional
    @Override
    public TicketOp.TicketOpGetResponse getAll() {
        return null;//TicketOp.TicketOpGetResponse //todo
    }
//   @Override
//   public Ticket update(Ticket ticket, TicketPriority priority, User asignee, Category category, Comment comment, String summary,
//                        String description, TicketStatus status, Timestamp openDate, User creator, String resolution, Timestamp deadline){
//      ticket.setAssignee(asignee);
//      ticket.setPriority(priority);
//      ticket.setCategory(category);
//      ticket.setComment(comment);
//      ticket.setSummary(summary);
//      ticket.setDescription(description);
//      ticket.setStatus(status);
//      ticket.setOpenDate(openDate);
//      ticket.setCreator(creator);
//      ticket.setResolution(resolution);
//      ticket.setDeadline(deadline);
//
//      return ticket;
//   }



}
