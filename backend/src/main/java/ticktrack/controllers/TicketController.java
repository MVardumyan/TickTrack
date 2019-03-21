package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.interfaces.ITicketManager;
import ticktrack.managers.TicketManager;
import ticktrack.proto.Msg;

import static common.helpers.CustomJsonParser.jsonToProtobuf;
import static common.helpers.CustomJsonParser.protobufToJson;
import static ticktrack.util.ResponseHandler.buildFailureResponse;
import static ticktrack.util.ResponseHandler.wrapCommonResponseIntoMsg;

@Controller
@RequestMapping(value = "backend/v1/Tickets")
public class TicketController {
    private final ITicketManager ticketManager;
    private final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    public TicketController(ITicketManager ticketManager) { this.ticketManager = ticketManager; }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String addTicket(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasTicketOperation() && request.getTicketOperation().hasTicketOpCreateRequest()) {
                Msg result = ticketManager.create(request.getTicketOperation().getTicketOpCreateRequest());
                return protobufToJson(result);
            }

            logger.warn("No create ticket request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No create ticket request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling create ticket request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String updateTicket(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasTicketOperation() && request.getTicketOperation().hasTicketOpUpdateRequest()) {
                Msg result = ticketManager.updateTicket(request.getTicketOperation().getTicketOpUpdateRequest());
                return protobufToJson(result);
            }

            logger.warn("No update ticket request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No update ticket request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling update ticket request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/addComment", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String addComment(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasTicketOperation() && request.getTicketOperation().hasTicketOpAddComment()) {
                Msg result = ticketManager.addComment(request.getTicketOperation().getTicketOpAddComment());
                return protobufToJson(result);
            }

            logger.warn("No add comment request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No add comment request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling create user request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
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
