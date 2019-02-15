package classes.beans;

import classes.entities.*;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.interfaces.ITicketManager;
import classes.repositories.CommentRepository;
import classes.repositories.TicketRepository;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ticktrack.proto.CommonResponse;
import ticktrack.proto.TicketInfo;
import ticktrack.proto.TicketOp;

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

    @Override
    public CommonResponse create(TicketOp.TicketOpCreateRequest request) {
        return null;
    }

    @Transactional
    @Override
    public CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request) {
        Optional<Ticket> result = ticketRepository.findByID(request.getTicketID());
        TicketPriority priority = result.get().getPriority();

        switch(request.getParamName()) {
            case Summary:

        }

        //User assignee = request.getParamName();
        String summary = result.get().getSummary();
        String description = result.get().getDescription();
        TicketStatus status = result.get().getStatus();
        Timestamp openDate = result.get().getOpenDate();
        User creator = result.get().getCreator();
        String resolution = result.get().getResolution();
        Timestamp deadline = result.get().getDeadline();
        //Timestamp closeDate = result.get().getCloseDate();

        //result.get().setAssignee(assignee);
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
//            result.get().setComment(comment);
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
            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Failure)
               .build();
        }
        return response;
    }

    @Transactional
    @Override
    public TicketOp.TicketOpGetResponse get(TicketOp.TicketOpGetRequest request) {
        Optional<Ticket> result = ticketRepository.findByID(request.getTicketId());
        if (result.isPresent()) {
            logger.debug("Query for {} ticket received", request.getTicketId());
            return null; //todo
        } else {
            logger.debug("Ticket {} not found", request.getTicketId());
            return null;
        }
    }

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
