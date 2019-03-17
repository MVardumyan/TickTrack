package ticktrack.frontend.controller;

import com.google.protobuf.util.JsonFormat;
import common.enums.UserRole;
import common.helpers.CustomJsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ticktrack.frontend.attributes.User;
import ticktrack.frontend.util.OkHttpRequestHandler;
import ticktrack.proto.Msg;
import ticktrack.proto.Msg.UserOp.UserOpValidatePasswordLink;

import java.io.IOException;

import static common.helpers.CustomJsonParser.jsonToProtobuf;
import static common.helpers.CustomJsonParser.protobufToJson;
import static ticktrack.frontend.util.OkHttpRequestHandler.buildRequestWithoutBody;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Success;

@Controller
public class PersonalInfoController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(NewTicketController.class);
    private String backendURL = "http://localhost:9201/backend/v1/";

    @Autowired
    public PersonalInfoController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/personalInfo", method = RequestMethod.GET)
    public String displayPersonalInfoPage(ModelMap model, @SessionAttribute User user) {
        return "redirect:/personalInfo/" + user.getUsername();
    }

    @RequestMapping(value = "/personalInfo/{username}", method = RequestMethod.GET)
    public String displayPersonalInfoPage(ModelMap model, @PathVariable("username") String username, @SessionAttribute User user) {
        Request request = buildRequestWithoutBody(backendURL + "users/getUser/" + username);
        showPersonalInfo(request, model, user);
        return "personalInfo";
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.GET)
    String displayUpdateUserInfo(ModelMap model, @SessionAttribute("user") User user) {
        return "redirect:/updateUserInfo/" + user.getUsername();
    }

    @RequestMapping(value = "/updateUserInfo/{username}", method = RequestMethod.GET)
    String displayUpdateUserInfo(ModelMap model, @PathVariable("username") String username, @SessionAttribute("user") User user) {
        Request request = buildRequestWithoutBody(backendURL + "users/getUser/" + username);
        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());
            model.put("username", username);
            if (result != null) {
                model.put("info", result.getUserOperation().getUserOpGetResponse().getUserInfo(0));
            } else {
                logger.error("User info is null!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "updateUserInfo";

    }

    @RequestMapping(value = "/updateUsersInfo/{username}", method = RequestMethod.POST)
    String updateUserInfo(ModelMap model, @PathVariable("username") String username,
                          @SessionAttribute("user") User user,
                          @RequestParam() String firstName,
                          @RequestParam() String lastName,
                          @RequestParam() String email,
                          @RequestParam(required = false) String role) {

        Msg.UserOp.UserOpUpdateRequest.Builder requestMessage = Msg.UserOp.UserOpUpdateRequest.newBuilder();
        requestMessage.setUsername(username)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email);
        if (role != null) {
            try {
                requestMessage.setRole(Msg.UserRole.valueOf(role));
            } catch (IllegalArgumentException e) {
                logger.error("There is no role input!");
            }
        }
//        if (gender!=null){
//            requestMessage.setGender(Msg.UserOp.Gender.valueOf(gender));
//        }

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            configurePersonalInfo(model, username, user, response);
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }


        return "personalInfo";
    }

    private void configurePersonalInfo(ModelMap model, @PathVariable("username") String username, @SessionAttribute("user") User user, Response response) throws IOException {
        if (response.code() == 200) {
            Msg msg = jsonToProtobuf(response.body().string());
            if (msg != null) {
                if (!user.getRole().equals(UserRole.RegularUser)) {
                    model.put("notRegular", true);
                    if (user.getRole().equals(UserRole.Admin)) {
                        model.put("admin", true);
                    }
                }
                Request request = buildRequestWithoutBody(backendURL + "users/getUser/" + username);
                showPersonalInfo(request, model, user);
            }
        } else {
            logger.warn("Error received from backend, unable to get search result: {}", response.message());
        }
    }

    private Msg wrapIntoMsg(Msg.UserOp.UserOpUpdateRequest.Builder requestMessage) {
        return Msg.newBuilder().setUserOperation(Msg.UserOp.newBuilder().setUserOpUpdateRequest(requestMessage))
                .build();
    }

    /////////////////////////////////////DEACTIVATE

    @RequestMapping(value = "/deactivate/{username}", method = RequestMethod.GET)
    String deactivate(ModelMap model, @PathVariable("username") String username, @SessionAttribute("user") User user
    ) {
        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "users/deactivate/" + username)
        ).execute()) {
            configurePersonalInfo(model, username, user, response);
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }


        return "personalInfo";
    }

    /////////////////////////////////////CHANGE PASSWORD
    @RequestMapping(value = "/getChangePasswordLink", method = RequestMethod.GET)
    String getChangePasswordLink(ModelMap model, @SessionAttribute User user) {
        Request request = buildRequestWithoutBody(
                backendURL + "users/generateChangePasswordLink/" + user.getUsername()
        );

        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());

            if (result != null && result.hasCommonResponse()) {
                if (result.getCommonResponse().getResponseType().equals(Success)) {
                    if (user.getRole().equals(UserRole.Admin)) {
                        model.put("admin", true);
                    } else {
                        model.put("admin", false);
                    }

                    return "passwordSuccess";
                }
            }

            return "error";

        } catch (IOException e) {
            logger.error("Internal error, unable to get change password link", e);
            return "error";
        }
    }

    @RequestMapping(value = "/changePassword/{link}", method = RequestMethod.GET)
    String displayChangePassword(ModelMap model, @SessionAttribute("user") User user, @PathVariable("link") String link) {
        UserOpValidatePasswordLink requestMessage = UserOpValidatePasswordLink.newBuilder()
                .setUsername(user.getUsername())
                .setLink(link)
                .build();

        Request request = OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/validatePasswordLink",
                protobufToJson(wrapIntoMsg(requestMessage)));

        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());

            if (result != null && result.hasCommonResponse()
                    && result.getCommonResponse().getResponseType().equals(Success)) {

                if (user.getRole().equals(UserRole.Admin)) {
                    model.put("admin", true);
                }
                return "changePassword";
            }

            return "error";
        } catch (IOException e) {
            logger.error("Internal error, unable to get password validation response", e);
            return "error";
        }
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    String changePassword(ModelMap model, @SessionAttribute("user") User user,
                          @RequestParam() String oldPassword,
                          @RequestParam() String newPassword) {

        Msg.UserOp.UserOpChangePassword.Builder requestMessage = Msg.UserOp.UserOpChangePassword.newBuilder();
        requestMessage.setUsername(user.getUsername());

        if (oldPassword != null) {
            requestMessage.setOldPassword(oldPassword);
            requestMessage.setNewPassword(newPassword);
        } else {
            return "error";
        }

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/changePassword", CustomJsonParser.protobufToJson(wrapPasswordIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    Request request = buildRequestWithoutBody(backendURL + "users/getUser/" + user.getUsername());
                    showPersonalInfo(request, model, user);
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        return "personalInfo";
    }

    private Msg wrapIntoMsg(UserOpValidatePasswordLink message) {
        return Msg.newBuilder()
                .setUserOperation(
                        Msg.UserOp.newBuilder()
                                .setUserOpValidatePasswordLink(message)
                ).build();
    }

    private Msg wrapPasswordIntoMsg(Msg.UserOp.UserOpChangePassword.Builder requestMessage) {
        return Msg.newBuilder().setUserOperation(Msg.UserOp.newBuilder().setUserOpChangePassword(requestMessage))
                .build();
    }

    private void showPersonalInfo(Request request, ModelMap model, @SessionAttribute User user) {
        try (Response response = httpClient.newCall(request).execute()) {
            Msg.Builder builder = Msg.newBuilder();
            JsonFormat.parser().merge(response.body().string(), builder);
            Msg result = builder.build();
            model.put("info", result.getUserOperation().getUserOpGetResponse().getUserInfo(0));
            if (user.getRole().equals(UserRole.Admin) || user.getUsername().equals(result.getUserOperation()
                    .getUserOpGetResponse()
                    .getUserInfo(0)
                    .getUsername())) {
                model.put("show", true);
                model.put("showPass", true);
            }
            if (user.getRole().equals(UserRole.Admin) && !user.getUsername().equals(result.getUserOperation()
                    .getUserOpGetResponse()
                    .getUserInfo(0)
                    .getUsername())) {
                model.put("showPass", false);
            }
            if (user.getRole().equals(UserRole.Admin)) {
                model.put("admin", true);
            } else {
                model.put("admin", false);
            }
            if (result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getIsActive()) {
                model.put("active", true);
                model.put("deactivated", false);
            } else {
                model.put("active", false);
                model.put("deactivated", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
