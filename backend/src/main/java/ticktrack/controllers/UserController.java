package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.managers.UserManager;
import ticktrack.proto.Msg;

import static ticktrack.util.CustomJsonParser.*;
import static ticktrack.util.ResponseHandler.*;

@Controller
@RequestMapping(value = "backend/v1/users")
public class UserController {
    private final UserManager userManager;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ResponseBody
    public String addUser(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpCreateRequest()) {
                Msg.CommonResponse result = userManager.create(request.getUserOperation().getUserOpCreateRequest());
                return protobufToJson(wrapCommonResponseIntoMsg(result));
            }

            logger.warn("No create user request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No create user request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling create user request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    @ResponseBody
    public String getUser(@RequestParam(name = "username") String username) {
        Msg.UserOp.UserOpGetResponse result = userManager.get(username);

        return protobufToJson(wrapIntoMsg(result));
    }

    @RequestMapping(value = "/getUsersByRole")
    @ResponseBody
    public String getUsersByRole(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpGetByCriteria()) {
                Msg.UserOp.UserOpGetResponse result = userManager.getByCriteria(request.getUserOperation().getUserOpGetByCriteria());
                return protobufToJson(wrapIntoMsg(result));
            }

            logger.warn("No get user by role request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No get user by role request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling get user by role request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    private Msg wrapIntoMsg(Msg.UserOp.UserOpGetResponse message) {
        return Msg.newBuilder()
                .setUserOperation(
                        Msg.UserOp.newBuilder()
                                .setUserOpGetResponse(message)
                ).build();
    }

}
