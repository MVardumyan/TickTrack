package ticktrack.util;

import ticktrack.entities.Ticket;
import ticktrack.proto.Msg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class contains util methods,
 * used in controllers for wrapping and handling different types of protobuf messages
 */
public final class ResponseHandler {
   /**
    * Method wraps each ticket entity from query result to protobuf type TicketInfo
    * and includes it in protobuf type SearchOpResponse
    * @param result List of Ticket entities
    * @return protobuf message SearchOpResponse, containing given tickets information
    */
   public static Msg.SearchOp.SearchOpResponse buildTicketResponseFromQueryResult(List<Ticket> result) {
      Msg.SearchOp.SearchOpResponse.Builder responseBuilder = Msg.SearchOp.SearchOpResponse.newBuilder();

      result.stream().map(ResponseHandler::buildTicketInfo).forEach(responseBuilder::addTicketInfo);

      return responseBuilder.build();
   }

   /**
    * Method builds parses given Ticket entity to protobuf type TicketInfo
    * @param ticket entity that will be parsed to TicketInfo
    * @return protobuf type TicketInfo
    */
   public static Msg.TicketInfo buildTicketInfo(Ticket ticket) {
      Msg.TicketInfo.Builder ticketMessage = Msg.TicketInfo.newBuilder()
         .setTicketID(ticket.getID())
         .setSummary(ticket.getSummary())
         .setDescription(ticket.getDescription())
         .setCategory(ticket.getCategory().getName())
         .setCreator(ticket.getCreator().getUsername())
         .setOpenDate(ticket.getOpenDate().toString())
         .setPriority(Msg.TicketPriority.valueOf(ticket.getPriority().toString()))
         .setStatus(Msg.TicketStatus.valueOf(ticket.getStatus().toString()));

      if(ticket.getCommentList() != null){
         ticketMessage.addAllComment(
                 ticket.getCommentList().stream().map(
                         comment -> Msg.Comment.newBuilder()
                                 .setTime(comment.getTimestamp().toString())
                                 .setText(comment.getText())
                                 .setUsername(comment.getUsername())
                                 .build()
                 ).collect(Collectors.toList())
         );
      }

      if (ticket.getAssignee() != null) {
         ticketMessage.setAssignee(ticket.getAssignee().getUsername());
      }
      if (ticket.getCloseDate() != null) {
         ticketMessage.setCloseDate(ticket.getCloseDate().toString());
      }
      if (ticket.getDeadline() != null) {
         ticketMessage.setDeadline(ticket.getDeadline().toString());
      }
      if (ticket.getGroup() != null) {
         ticketMessage.setGroup(ticket.getGroup().getName());
      }
      if (ticket.getResolution() != null) {
         ticketMessage.setResolution(ticket.getResolution());
      }

      return ticketMessage.build();
   }

   /**
    * Method builds protobuf message CommonResponse with responseType Success
    * @param responseText is included in response
    * @return protobuf type CommonResponse
    */
   public static Msg.CommonResponse buildSuccessResponse(String responseText) {
      return Msg.CommonResponse.newBuilder()
              .setResponseText(responseText)
              .setResponseType(Msg.CommonResponse.ResponseType.Success)
              .build();
   }

   /**
    * Method builds protobuf message CommonResponse with responseType Failure
    * @param responseText is included in response
    * @return protobuf type CommonResponse
    */
   public static Msg.CommonResponse buildFailureResponse(String responseText) {
      return Msg.CommonResponse.newBuilder()
              .setResponseText(responseText)
              .setResponseType(Msg.CommonResponse.ResponseType.Failure)
              .build();
   }

   public static Msg wrapCommonResponseIntoMsg(Msg.CommonResponse message) {
      return Msg.newBuilder()
              .setCommonResponse(message)
              .build();
   }
}
