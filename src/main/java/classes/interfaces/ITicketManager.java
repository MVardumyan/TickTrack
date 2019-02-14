package classes.interfaces;

import classes.entities.*;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import ticktrack.proto.CommonResponse;
import ticktrack.proto.TicketOp;

import java.sql.Timestamp;

public interface ITicketManager {
   CommonResponse create(TicketOp.TicketOpCreateRequest request);

   CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request);

   CommonResponse addComment(TicketOp.TicketOpAddComment request);

    TicketOp.TicketOpGetResponse get(TicketOp.TicketOpGetRequest request);

    TicketOp.TicketOpGetResponse getAll();
}
