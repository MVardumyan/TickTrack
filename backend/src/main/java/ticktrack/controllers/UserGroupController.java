package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.interfaces.IUserGroupManager;
import ticktrack.proto.Msg;

import static common.helpers.CustomJsonParser.jsonToProtobuf;
import static common.helpers.CustomJsonParser.protobufToJson;
import static ticktrack.util.ResponseHandler.*;

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
    ResponseEntity addUserGroup(@PathVariable("groupName") String groupName) {
        Msg.CommonResponse result = userGroupManager.createUserGroup(groupName);
        return processManagerResponse(result);
    }

    @RequestMapping(path = "/delete/{groupName}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    ResponseEntity deleteUserGroup(@PathVariable("groupName") String groupName) {
        Msg.CommonResponse result = userGroupManager.deleteUserGroup(groupName);
        return processManagerResponse(result);
    }

    @RequestMapping(path = "/changeName", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity changeUserGroupName(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        Msg.CommonResponse result;

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasUserGroupOperation()
                && request.getUserGroupOperation().hasUserGroupOpUpdateRequest()) {
            result = userGroupManager.changeName(
                    request.getUserGroupOperation().getUserGroupOpUpdateRequest()
            );

            return processManagerResponse(result);
        }

        return buildInvalidProtobufContentResponse("Request message should contain UserGroupOp.UserGroupOpUpdateRequest type");
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
