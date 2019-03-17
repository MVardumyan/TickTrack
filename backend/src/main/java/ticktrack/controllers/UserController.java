package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.managers.UserManager;
import ticktrack.proto.Msg;

import static common.helpers.CustomJsonParser.*;
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

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String addUser(@RequestBody String jsonRequest) {
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

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String updateUser(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpUpdateRequest()) {
                Msg result = userManager.update(request.getUserOperation().getUserOpUpdateRequest());
                return protobufToJson(result);
            }

            logger.warn("No update user request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No update user request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling update user request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String changePassword(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpChangePassword()) {
                Msg result = userManager.changePassword(request.getUserOperation().getUserOpChangePassword());
                return protobufToJson(result);
            }

            logger.warn("No change password request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No change password request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling change password request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/generateChangePasswordLink/{username}", method = RequestMethod.GET)
    @ResponseBody
    String generateChangePasswordLink(@PathVariable("username") String username) {
        Msg.CommonResponse result = userManager.generateChangePasswordLink(username);

        return protobufToJson(wrapCommonResponseIntoMsg(result));
    }

    @RequestMapping(value = "/validatePasswordLink", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String validatePasswordLink(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpValidatePasswordLink()) {
                Msg.CommonResponse response = userManager.validatePasswordLink(request.getUserOperation().getUserOpValidatePasswordLink());
                return protobufToJson(wrapCommonResponseIntoMsg(response));
            }

            logger.warn("No validate password link request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No validate password link request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling change password request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/deactivate/{username}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String deactivate(@PathVariable("username") String username) {
        Msg.CommonResponse result = userManager.deactivate(username);

        return protobufToJson(wrapCommonResponseIntoMsg(result));
    }

    @RequestMapping(value = "/getUser/{username}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String getUser(@PathVariable("username") String username) {
        Msg.UserOp.UserOpGetResponse result = userManager.get(username);

        return protobufToJson(wrapIntoMsg(result));
    }

    @RequestMapping(value = "/getUsersByRole", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String getUsersByRole(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpGetByRoleRequest()) {
                Msg.UserOp.UserOpGetResponse result = userManager.getByRole(request.getUserOperation().getUserOpGetByRoleRequest());
                return protobufToJson(wrapIntoMsg(result));
            }

            logger.warn("No get user by role request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No get user by role request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling get user by role request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/validateLogin", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String validateLogin(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasLoginRequest()) {
                Msg.CommonResponse result = userManager.validateLoginInformation(request.getLoginRequest());
                return protobufToJson(wrapCommonResponseIntoMsg(result));
            }

            logger.warn("No login request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No login request found")));
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
