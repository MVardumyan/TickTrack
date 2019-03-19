package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.interfaces.IUserGroupManager;
import ticktrack.managers.UserGroupManager;
import ticktrack.proto.Msg;

import static common.helpers.CustomJsonParser.jsonToProtobuf;
import static common.helpers.CustomJsonParser.protobufToJson;
import static ticktrack.util.ResponseHandler.buildFailureResponse;
import static ticktrack.util.ResponseHandler.wrapCommonResponseIntoMsg;

@Controller
@RequestMapping(value = "backend/v1/userGroups")
public class UserGroupController {
    private final IUserGroupManager userGroupManager;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserGroupController(IUserGroupManager userGroupManager) {
        this.userGroupManager = userGroupManager;
    }

    @RequestMapping(value = "/add/{groupName}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String addUserGroup(@PathVariable("groupName") String groupName) {
        Msg.CommonResponse result = userGroupManager.createUserGroup(groupName);
        return protobufToJson(wrapCommonResponseIntoMsg(result));
    }

    @RequestMapping(path = "/delete/{groupName}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String deleteUserGroup(@PathVariable("groupName") String groupName) {
        Msg.CommonResponse result = userGroupManager.deleteUserGroup(groupName);
        return protobufToJson(wrapCommonResponseIntoMsg(result));
    }

    @RequestMapping(path = "/changeName", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String changeUserGroupName(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            Msg.CommonResponse result;

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasUserGroupOperation()
                    && request.getUserGroupOperation().hasUserGroupOpUpdateRequest()) {
                result = userGroupManager.changeName(
                        request.getUserGroupOperation().getUserGroupOpUpdateRequest()
                );
            } else {
                result = buildFailureResponse("Request message should contain UserGroupOp.UserGroupOpUpdateRequest type");
            }

            return protobufToJson(wrapCommonResponseIntoMsg(result));
        } catch (Throwable t) {
            logger.error("Exception appear while handling search request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String getUser() {
        Msg.UserGroupOp.UserGroupOpGetAllResponse result = userGroupManager.getAll();

        return protobufToJson(wrapIntoMsg(result));
    }

    private Msg wrapIntoMsg(Msg.UserGroupOp.UserGroupOpGetAllResponse message) {
        return Msg.newBuilder()
                .setUserGroupOperation(
                        Msg.UserGroupOp.newBuilder()
                                .setUserGroupOpGetAllResponse(message)
                ).build();
    }
}
