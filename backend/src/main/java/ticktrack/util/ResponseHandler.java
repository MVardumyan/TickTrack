package ticktrack.util;

import ticktrack.entities.Ticket;
import ticktrack.proto.Msg;

import java.util.List;
import java.util.stream.Collectors;

public class ResponseHandler {
   public static Msg.SearchOp.SearchOpResponse composeResponseMessageFromQueryResult(List<Ticket> result) {
      Msg.SearchOp.SearchOpResponse.Builder responseBuilder = Msg.SearchOp.SearchOpResponse.newBuilder();

      result.stream().map(ticket -> {
         Msg.TicketInfo.Builder ticketMessage = Msg.TicketInfo.newBuilder()
            .setTicketID(ticket.getID())
            .setSummary(ticket.getSummary())
            .setDescription(ticket.getDescription())
            .setCategory(ticket.getCategory().getName())
            .setCreator(ticket.getCreator().getUsername())
            .setOpenDate(ticket.getOpenDate().getTime())
            .setPriority(Msg.TicketPriority.valueOf(ticket.getPriority().toString()))
            .setStatus(Msg.TicketStatus.valueOf(ticket.getStatus().toString()))
            .addAllComment(
               ticket.getCommentList().stream().map(
                  comment -> Msg.Comment.newBuilder()
                     .setTime(comment.getTimestamp().getTime())
                     .setText(comment.getText())
                     .setUsername(comment.getUsername())
                     .build()
               ).collect(Collectors.toList())
            );

         if (ticket.getAssignee() != null) {
            ticketMessage.setAssignee(ticket.getAssignee().getUsername());
         }
         if (ticket.getCloseDate() != null) {
            ticketMessage.setCloseDate(ticket.getCloseDate().getTime());
         }
         if (ticket.getDeadline() != null) {
            ticketMessage.setDeadline(ticket.getDeadline().getTime());
         }
         if (ticket.getGroup() != null) {
            ticketMessage.setGroup(ticket.getGroup().getName());
         }
         if (ticket.getResolution() != null) {
            ticketMessage.setResolution(ticket.getResolution());
         }

         return ticketMessage.build();
      }).forEach(responseBuilder::addTicketInfo);

      return responseBuilder.build();
   }
}
