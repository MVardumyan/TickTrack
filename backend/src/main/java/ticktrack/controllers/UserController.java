package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.interfaces.IUserManager;
import ticktrack.proto.Msg;

import static common.helpers.CustomJsonParser.*;
import static ticktrack.util.ResponseHandler.*;

@Controller
@RequestMapping(value = "backend/v1/users")
public class UserController {
    private final IUserManager userManager;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(IUserManager userManager) {
        this.userManager = userManager;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity addUser(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpCreateRequest()) {

            Msg.CommonResponse result = userManager.create(request.getUserOperation().getUserOpCreateRequest());
            return processManagerResponse(result);
        }

        logger.warn("No create user request found");
        return buildInvalidProtobufContentResponse("No create user request found");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity updateUser(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpUpdateRequest()) {
            Msg result = userManager.update(request.getUserOperation().getUserOpUpdateRequest());
            if (result.hasCommonResponse()) {
                return ResponseEntity
                        .badRequest()
                        .body(protobufToJson(result));
            }
            return ResponseEntity
                    .ok(protobufToJson(result));
        }

        logger.warn("No update user request found");
        return buildInvalidProtobufContentResponse("No update user request found");
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity changePassword(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpChangePassword()) {
            Msg.CommonResponse result = userManager.changePassword(request.getUserOperation().getUserOpChangePassword());
            return processManagerResponse(result);
        }

        logger.warn("No change password request found");
        return buildInvalidProtobufContentResponse("No change password request found");
    }

    @RequestMapping(value = "/generateChangePasswordLink/{username}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity generateChangePasswordLink(@PathVariable("username") String username) {
        Msg.CommonResponse result = userManager.generateChangePasswordLink(username);

        return processManagerResponse(result);
    }

    @RequestMapping(value = "/validatePasswordLink", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity validatePasswordLink(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpValidatePasswordLink()) {
            Msg.CommonResponse result = userManager.validatePasswordLink(request.getUserOperation().getUserOpValidatePasswordLink());
            return processManagerResponse(result);
        }

        logger.warn("No validate password link request found");
        return buildInvalidProtobufContentResponse("No validate password link request found");
    }

    @RequestMapping(value = "/deactivate/{username}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    ResponseEntity deactivate(@PathVariable("username") String username) {
        Msg.CommonResponse result = userManager.deactivate(username);

        return processManagerResponse(result);
    }

    @RequestMapping(value = "/getUser/{username}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String getUser(@PathVariable("username") String username) {
        Msg.UserOp.UserOpGetResponse result = userManager.get(username);

        return protobufToJson(wrapIntoMsg(result));
    }

    @RequestMapping(value = "/getUsersByRole/{page}/{size}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String getUsersByRole(@RequestBody String jsonRequest,
                          @PathVariable("page") Integer page,
                          @PathVariable("size") Integer size) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);
    ResponseEntity getUsersByRole(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasUserOperation() && request.getUserOperation().hasUserOpGetByRoleRequest()) {
            Msg.UserOp.UserOpGetResponse result = userManager.getByRole(request.getUserOperation().getUserOpGetByRoleRequest(),page,size);
            return ResponseEntity
                    .ok(protobufToJson(wrapIntoMsg(result)));
        }

        logger.warn("No get user by role request found");
        return buildInvalidProtobufContentResponse("No get user by role request found");
    }

    @RequestMapping(value = "/validateLogin", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity validateLogin(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasLoginRequest()) {
            Msg.CommonResponse result = userManager.validateLoginInformation(request.getLoginRequest());
            return processManagerResponse(result);
        }

        logger.warn("No login request found");
        return buildInvalidProtobufContentResponse("No login request found");
    }

    private Msg wrapIntoMsg(Msg.UserOp.UserOpGetResponse message) {
        return Msg.newBuilder()
                .setUserOperation(
                        Msg.UserOp.newBuilder()
                                .setUserOpGetResponse(message)
                ).build();
    }

}
