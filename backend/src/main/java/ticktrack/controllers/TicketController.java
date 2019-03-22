package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.interfaces.ITicketManager;
import ticktrack.managers.TicketManager;
import ticktrack.proto.Msg;

import static common.helpers.CustomJsonParser.jsonToProtobuf;
import static common.helpers.CustomJsonParser.protobufToJson;
import static ticktrack.util.ResponseHandler.*;

@Controller
@RequestMapping(value = "backend/v1/Tickets")
public class TicketController {
    private final ITicketManager ticketManager;
    private final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    public TicketController(ITicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity addTicket(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasTicketOperation() && request.getTicketOperation().hasTicketOpCreateRequest()) {
            Msg result = ticketManager.create(request.getTicketOperation().getTicketOpCreateRequest());
            if (result.hasCommonResponse()) {
                return ResponseEntity
                        .badRequest()
                        .body(protobufToJson(result));
            }
            return ResponseEntity
                    .ok(protobufToJson(result));
        }

        logger.warn("No create ticket request found");
        return buildInvalidProtobufContentResponse("No create ticket request found");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity updateTicket(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasTicketOperation() && request.getTicketOperation().hasTicketOpUpdateRequest()) {
            Msg result = ticketManager.updateTicket(request.getTicketOperation().getTicketOpUpdateRequest());
            if (result.hasCommonResponse()) {
                return ResponseEntity
                        .badRequest()
                        .body(protobufToJson(result));
            }
            return ResponseEntity
                    .ok(protobufToJson(result));
        }

        logger.warn("No update ticket request found");
        return buildInvalidProtobufContentResponse("No update ticket request found");
    }

    @RequestMapping(value = "/addComment", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity addComment(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasTicketOperation() && request.getTicketOperation().hasTicketOpAddComment()) {
            Msg result = ticketManager.addComment(request.getTicketOperation().getTicketOpAddComment());
            if (result.hasCommonResponse()) {
                return ResponseEntity
                        .badRequest()
                        .body(protobufToJson(result));
            }
            return ResponseEntity
                    .ok(protobufToJson(result));
        }

        logger.warn("No add comment request found");
        return buildInvalidProtobufContentResponse("No add comment request found");
    }

    @RequestMapping(value = "/getTicket/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String getTicket(@PathVariable("id") long id) {
        Msg.TicketInfo result = ticketManager.get(id);

        return protobufToJson(wrapIntoMsg(result));
    }

    private Msg wrapIntoMsg(Msg.TicketInfo message) {
        return Msg.newBuilder()
                .setTicketInfo(message).build();
    }
}
