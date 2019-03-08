package ticktrack.util;

import ticktrack.entities.Ticket;
import ticktrack.proto.Msg;

import java.util.List;
import java.util.stream.Collectors;

public final class ResponseHandler {
   public static Msg.SearchOp.SearchOpResponse buildTicketResponseFromQueryResult(List<Ticket> result) {
      Msg.SearchOp.SearchOpResponse.Builder responseBuilder = Msg.SearchOp.SearchOpResponse.newBuilder();

      result.stream().map(ResponseHandler::buildTicketInfo).forEach(responseBuilder::addTicketInfo);

      return responseBuilder.build();
   }

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
                                 .setTime(comment.getTimestamp().getTime())
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

   public static Msg.CommonResponse buildSuccessResponse(String responseText) {
      return Msg.CommonResponse.newBuilder()
              .setResponseText(responseText)
              .setResponseType(Msg.CommonResponse.ResponseType.Success)
              .build();
   }

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
