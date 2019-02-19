package classes.interfaces;

import classes.entities.Ticket;

import static ticktrack.proto.Msg.*;

public interface ITicketManager {
   CommonResponse create(TicketOp.TicketOpCreateRequest request);

   CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request);

   CommonResponse addComment(TicketOp.TicketOpAddComment request);

    Ticket get(long ticket_id);

    TicketOp.TicketOpGetResponse getAll();
}
