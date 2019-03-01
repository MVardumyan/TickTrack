package ticktrack.interfaces;

import static ticktrack.proto.Msg.*;

public interface ITicketManager {
    CommonResponse create(TicketOp.TicketOpCreateRequest request);

    CommonResponse updateTicket(TicketOp.TicketOpUpdateRequest request);

    CommonResponse addComment(TicketOp.TicketOpAddComment request);

    TicketInfo get(long ticket_id);

    SearchOp.SearchOpResponse getAll();
}