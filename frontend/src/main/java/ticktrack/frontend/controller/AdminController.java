package ticktrack.frontend.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ticktrack.frontend.util.OkHttpRequestHandler;
import ticktrack.proto.Msg;
import ticktrack.proto.Msg.UserOp.UserOpGetByRoleRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static common.helpers.CustomJsonParser.*;
import static ticktrack.proto.Msg.UserRole.Admin;
import static ticktrack.proto.Msg.UserRole.BusinessUser;
import static ticktrack.proto.Msg.UserRole.RegularUser;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private String backendURL = "http://localhost:9001/backend/v1/";

    @Autowired
    public AdminController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    String displaySearchPage(ModelMap model) {

        return "adminMain";
    }

    @RequestMapping(value = "/userManagement", method = RequestMethod.GET)
    String displayUserManagementPage(ModelMap model) {
        UserOpGetByRoleRequest message = UserOpGetByRoleRequest.newBuilder()
                .setCriteria(UserOpGetByRoleRequest.Criteria.All)
                .build();

        Request request = OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/getUsersByRole",
                protobufToJson(wrapIntoMsg(message)));

        try (Response response = httpClient.newCall(request).execute()) {
            if(response.code()==200) {
                Msg result = jsonToProtobuf(response.body().string());

                if (result != null) {
                    List<Msg.UserOp.UserOpGetResponse.UserInfo> userInfoList = result.getUserOperation().getUserOpGetResponse().getUserInfoList();

                    model.put("regularUserInfo", userInfoList
                            .stream()
                            .filter(userInfo -> userInfo.getRole().equals(RegularUser))
                            .collect(Collectors.toList()));

                    model.put("businessUserInfo", userInfoList
                            .stream()
                            .filter(userInfo -> userInfo.getRole().equals(BusinessUser))
                            .collect(Collectors.toList()));

                    model.put("adminInfo", userInfoList
                            .stream()
                            .filter(userInfo -> userInfo.getRole().equals(Admin))
                            .collect(Collectors.toList()));

                    return "userManagement";
                }
            }
            return "error";
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            return "error";
        }
    }

    private Msg wrapIntoMsg(UserOpGetByRoleRequest request) {
        return Msg.newBuilder()
                .setUserOperation(
                        Msg.UserOp.newBuilder()
                            .setUserOpGetByRoleRequest(request)
                ).build();
    }


}
