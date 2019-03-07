package ticktrack.interfaces;

import ticktrack.proto.Msg;

import static ticktrack.proto.Msg.*;

public interface ITicketManager {
    Msg create(TicketOp.TicketOpCreateRequest request);

    Msg updateTicket(TicketOp.TicketOpUpdateRequest request);

    CommonResponse addComment(TicketOp.TicketOpAddComment request);

    TicketInfo get(long ticket_id);

    SearchOp.SearchOpResponse getAll();
}
