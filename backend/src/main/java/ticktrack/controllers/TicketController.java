package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.managers.TicketManager;
import ticktrack.proto.Msg;

import static ticktrack.util.CustomJsonParser.jsonToProtobuf;
import static ticktrack.util.CustomJsonParser.protobufToJson;
import static ticktrack.util.ResponseHandler.buildFailureResponse;
import static ticktrack.util.ResponseHandler.wrapCommonResponseIntoMsg;

@Controller
@RequestMapping(value = "backend/v1/Tickets")
public class TicketController {
    private final TicketManager ticketManager;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public TicketController(TicketManager ticketpManagerManager, TicketManager ticketManager) { this.ticketManager = ticketManager; }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String addTicket(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasTicketOperation() && request.getTicketOperation().hasTicketOpCreateRequest()) {
                Msg.CommonResponse result = ticketManager.create(request.getTicketOperation().getTicketOpCreateRequest());
                return protobufToJson(wrapCommonResponseIntoMsg(result));
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
                Msg.CommonResponse result = ticketManager.updateTicket(request.getTicketOperation().getTicketOpUpdateRequest());
                return protobufToJson(wrapCommonResponseIntoMsg(result));
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
                Msg.CommonResponse result = ticketManager.addComment(request.getTicketOperation().getTicketOpAddComment());
                return protobufToJson(wrapCommonResponseIntoMsg(result));
            }

            logger.warn("No add comment request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No add comment request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling create user request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }
}
