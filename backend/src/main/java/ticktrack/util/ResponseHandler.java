package ticktrack.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import ticktrack.entities.Ticket;
import ticktrack.proto.Msg;

import java.util.List;
import java.util.stream.Collectors;

import static common.helpers.CustomJsonParser.protobufToJson;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Failure;

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

      result.stream().map(ticket -> buildTicketWithoutComments(ticket).build()).forEach(responseBuilder::addTicketInfo);

      return responseBuilder.build();
   }

   /**
    * Method parses given Ticket entity to protobuf message TicketInfo
    * @param ticket entity that will be parsed to TicketInfo
    * @return protobuf message TicketInfo
    */
   public static Msg.TicketInfo buildTicketInfo(Ticket ticket) {
      Msg.TicketInfo.Builder ticketMessage = buildTicketWithoutComments(ticket);

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

      return ticketMessage.build();
   }

   /**
    * Method builds ticket info without comments.
    * This is needed for search operations response for performance purposes.
    * @param ticket entity that will be parsed to TicketInfo
    * @return protobuf message TicketInfo's builder
    */
   private static Msg.TicketInfo.Builder buildTicketWithoutComments(Ticket ticket) {
      Msg.TicketInfo.Builder ticketMessage = Msg.TicketInfo.newBuilder()
         .setTicketID(ticket.getID())
         .setSummary(ticket.getSummary())
         .setDescription(ticket.getDescription())
         .setCategory(ticket.getCategory().getName())
         .setCreator(ticket.getCreator().getUsername())
         .setOpenDate(ticket.getOpenDate().toString())
         .setPriority(Msg.TicketPriority.valueOf(ticket.getPriority().toString()))
         .setStatus(Msg.TicketStatus.valueOf(ticket.getStatus().toString()));

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

      return ticketMessage;
   }

   /**
    * Method builds protobuf message CommonResponse with responseType Success
    * @param responseText is included in response
    * @return protobuf message CommonResponse
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
    * @return protobuf message CommonResponse
    */
   public static Msg.CommonResponse buildFailureResponse(String responseText) {
      return Msg.CommonResponse.newBuilder()
              .setResponseText(responseText)
              .setResponseType(Msg.CommonResponse.ResponseType.Failure)
              .build();
   }

   /**
    * Method for wrapping protobuf message CommonResponse into general type Msg.
    * @param message protobuf message CommonResponse
    * @return protobuf message Msg
    */
   public static Msg wrapCommonResponseIntoMsg(Msg.CommonResponse message) {
      return Msg.newBuilder()
              .setCommonResponse(message)
              .build();
   }

   /**
    * Method user by controllers for building http response entity in case of json to protobuf parse failure
    * @return http response entity; code - 400 Bad Request; body - CommonResponse with type failure
    */
   @NotNull
   public static ResponseEntity<String> buildFailedToParseResponse() {
      return ResponseEntity
              .badRequest()
              .body(
                      protobufToJson(
                              wrapCommonResponseIntoMsg(
                                      buildFailureResponse("Unable to parse request to protobuf")))
              );
   }

   /**
    * Method user by controllers for building http response entity in case of incorrect protobuf message type
    * @param message failure response message
    * @return http response entity; code - 400 Bad Request; body - CommonResponse with type failure
    */
   @NotNull
   public static ResponseEntity<String> buildInvalidProtobufContentResponse(String message) {
      return ResponseEntity
              .badRequest()
              .body(protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse(message))));
   }

   /**
    * Method user by controllers to handle CommonResponse, received from managers.
    * @param result protobuf message CommonResponse
    * @return http response entity. In case of Failure - 400 Bad Request; success - 200 Ok
    */
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
