package classes.beans;

import classes.entities.*;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.interfaces.ITicketManager;
import ticktrack.proto.CommonResponse;
import ticktrack.proto.TicketOp;

import java.sql.Timestamp;

public class TicketManager implements ITicketManager {
    @Override
    public CommonResponse create(TicketOp.TicketOpCreateRequest request) {
        return null;
    }

    @Override
    public CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request) {
        return null;
    }

    @Override
    public CommonResponse addComment(TicketOp.TicketOpAddComment request) {
        return null;
    }

    @Override
    public TicketOp.TicketOpGetResponse get(TicketOp.TicketOpGetRequest request) {
        return null;
    }

    @Override
    public TicketOp.TicketOpGetResponse getAll() {
        return null;
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
