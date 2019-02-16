package classes.interfaces;

import static ticktrack.proto.Msg.*;

public interface ITicketManager {
   CommonResponse create(TicketOp.TicketOpCreateRequest request);

   CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request);

   CommonResponse addComment(TicketOp.TicketOpAddComment request);

    TicketOp.TicketOpGetResponse get(long ticket_id);

    TicketOp.TicketOpGetResponse getAll();
}
