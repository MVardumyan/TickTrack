package ticktrack.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import ticktrack.entities.Ticket;
import ticktrack.proto.Msg;

import java.util.List;
import java.util.stream.Collectors;

import static common.helpers.CustomJsonParser.protobufToJson;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Failure;

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

   @NotNull
   public static ResponseEntity<String> buildFailedToParseResponse() {
      return ResponseEntity
              .badRequest()
              .body(protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Unable to parse request to protobuf"))));
   }

   @NotNull
   public static ResponseEntity<String> buildInvalidProtobufContentResponse(String s) {
      return ResponseEntity
              .badRequest()
              .body(protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse(s))));
   }

   @NotNull
   public static ResponseEntity processManagerResponse(Msg.CommonResponse result) {
      if (result.getResponseType().equals(Failure)) {
         return ResponseEntity
                 .badRequest()
                 .body(protobufToJson(wrapCommonResponseIntoMsg(result)));
      }
      return ResponseEntity
              .ok(protobufToJson(wrapCommonResponseIntoMsg(result)));
   }
}
