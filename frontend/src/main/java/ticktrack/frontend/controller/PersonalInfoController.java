package ticktrack.frontend.controller;

import common.enums.UserRole;
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
import ticktrack.proto.Msg;
import ticktrack.proto.Msg.UserOp.UserOpValidatePasswordLink;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static common.helpers.CustomJsonParser.*;
import static ticktrack.frontend.util.OkHttpRequestHandler.*;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Success;
import static ticktrack.proto.Msg.UserOp.*;

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
    String displayUpdateUserInfo(@SessionAttribute("user") User user) {
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
                logger.error("User not found");
            }
            if (user.getRole().equals(UserRole.Admin)) {
                model.put("admin", true);
            } else {
                model.put("admin", false);
            }
            if (user.getRole().equals(UserRole.Admin)) {
                model.put("admin", true);
            } else {
                model.put("admin", false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "updateUserInfo";

    }

    @RequestMapping(value = "/updateUsersInfo/{username}", method = RequestMethod.POST)
    String updateUserInfo(ModelMap model, @PathVariable("username") String username,
                          @SessionAttribute("user") User user,
                          @RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String email,
                          @RequestParam(required = false) String role) {

        UserOpUpdateRequest.Builder requestMessage = UserOpUpdateRequest.newBuilder();
        requestMessage.setUsername(username)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email);
        if (role != null) {
            try {
                requestMessage.setRole(Msg.UserRole.valueOf(role));
            } catch (IllegalArgumentException e) {
                logger.error("There is no role input!");
                model.put("error", "There is no role input!");
            }
        }
//        if (gender!=null){
//            requestMessage.setGender(Msg.UserOp.Gender.valueOf(gender));
//        }

        try (Response response = httpClient.newCall(buildRequestWithBody(backendURL + "users/update", protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            configurePersonalInfo(model, username, user, response);
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }
        return "personalInfo";
    }

    /////////////////////////////////////DEACTIVATE
    @RequestMapping(value = "/deactivate/{username}", method = RequestMethod.GET)
    String deactivate(ModelMap model, @PathVariable("username") String username, @SessionAttribute("user") User user
    ) {
        try (Response response = httpClient.newCall(buildRequestWithoutBody(backendURL + "users/deactivate/" + username)
        ).execute()) {
            configurePersonalInfo(model, username, user, response);
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }
        return "personalInfo";
    }

    /////////////////////////////////////CHANGE PASSWORD

    @RequestMapping(value = "/getChangePasswordLink", method = RequestMethod.GET)
    String getChangePasswordLink(ModelMap model, @SessionAttribute("user") User user) {
        Request request = buildRequestWithoutBody(
                backendURL + "users/generateChangePasswordLink/" + user.getUsername()
        );
        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());
            if (result != null && result.hasCommonResponse()) {
                if (result.getCommonResponse().getResponseType().equals(Success)) {
                    if(user.getRole().equals(UserRole.Admin)) {
                        model.put("admin", true);
                    } else {
                        model.put("admin", false);
                    }
                    return "passwordSuccess";
                }
            }
            logger.error("Unable to generate Change Password Link");
            model.put("error", "Unable to generate Change Password Link");
            return "error";
        } catch (IOException e) {
            logger.error("Internal error, unable to get change password link", e);
            model.put("error", "Internal error, unable to get change password link");
            return "error";
        }
    }

    @RequestMapping(value = "/fromLogin/getChangePasswordLink", method = RequestMethod.GET)
    String getChangePasswordLinkFromLogin(ModelMap model, @RequestParam String username) {
        Request request = buildRequestWithoutBody(
                backendURL + "users/generateChangePasswordLink/" + username
        );
        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());
            if (result != null && result.hasCommonResponse()) {
                if (result.getCommonResponse().getResponseType().equals(Success)) {
                    model.put("forgotPasswordResponse", "Link for password change is sent to your mail address");
                    fillLoginPage(model);
                    return "login";
                }
            }

            logger.error("Unable to generate Change Password Link");
            model.put("forgotPasswordResponse", "Unable to generate Change Password Link");
            fillLoginPage(model);
            return "login";
        } catch (IOException e) {
            logger.error("Internal error, unable to get change password link", e);
            model.put("forgotPasswordResponse", "Internal error, unable to get change password link");
            fillLoginPage(model);
            return "login";
        }
    }

    @RequestMapping(value = "/changePassword/{link}", method = RequestMethod.GET)
    String displayChangePassword(ModelMap model, HttpSession httpSession, @PathVariable("link") String link) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            model.put("link", link);
            return "changePasswordFromLogin";
        }

        UserOpValidatePasswordLink requestMessage = UserOpValidatePasswordLink.newBuilder()
                .setUsername(user.getUsername())
                .setLink(link)
                .build();
        Request request = buildRequestWithBody(backendURL + "users/validatePasswordLink",
                protobufToJson(wrapIntoMsg(requestMessage)));
        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());

            if (result != null && result.hasCommonResponse()
                    && result.getCommonResponse().getResponseType().equals(Success)) {

                if (user.getRole().equals(UserRole.Admin)) {
                    model.put("admin", true);
                }
                model.put("error", result.getCommonResponse().getResponseText());
                return "changePassword";
            }
            model.put("error", "Unable to change password");
            return "error";
        } catch (IOException e) {
            logger.error("Internal error, unable to get password validation response", e);
            model.put("error", "Internal error, unable to get password validation response");
            return "error";
        }
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    String changePassword(ModelMap model, @SessionAttribute("user") User user,
                          @RequestParam String newPassword) {
        UserOpChangePassword.Builder requestMessage = UserOpChangePassword.newBuilder();
        requestMessage.setUsername(user.getUsername());
        requestMessage.setNewPassword(newPassword);

        try (Response response = httpClient.newCall(buildRequestWithBody(backendURL + "users/changePassword", protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    Request request = buildRequestWithoutBody(backendURL + "users/getUser/" + user.getUsername());
                    showPersonalInfo(request, model, user);
                }
            } else {
                logger.warn("Internal error, unable to get search result: {}", response.message());
                model.put("error", "Internal error, unable to get search result");
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }
        return "personalInfo";
    }

    @RequestMapping(value = "/fromLogin/changePassword/{link}", method = RequestMethod.POST)
    String changePasswordFromLogin(ModelMap model,
                                   @RequestParam String username,
                                   @RequestParam String newPassword,
                                   @PathVariable("link") String link) {
        UserOpValidatePasswordLink validationMessage = UserOpValidatePasswordLink.newBuilder()
                .setUsername(username)
                .setLink(link)
                .build();
        Request validationRequest = buildRequestWithBody(backendURL + "users/validatePasswordLink",
                protobufToJson(wrapIntoMsg(validationMessage)));

        try (Response validationResponse = httpClient.newCall(validationRequest).execute()) {
            Msg validationResult = jsonToProtobuf(validationResponse.body().string());

            if (validationResult != null && validationResult.hasCommonResponse()
                    && validationResult.getCommonResponse().getResponseType().equals(Success)) {

                UserOpChangePassword.Builder changeMessage = UserOpChangePassword.newBuilder()
                        .setUsername(username)
                        .setNewPassword(newPassword);
                Request changeRequest = buildRequestWithBody(backendURL + "/users/changePassword", protobufToJson(wrapIntoMsg(changeMessage)));

                Response changeResponse = httpClient.newCall(changeRequest).execute();
                if (changeResponse.code() == 200) {
                    Msg changeResult = jsonToProtobuf(changeResponse.body().string());

                    if (changeResult != null && changeResult.hasCommonResponse() && changeResult.getCommonResponse().getResponseType().equals(Success)) {
                        model.put("forgotPasswordResponse", "Password successfully changed");
                    } else {
                        model.put("forgotPasswordResponse", "Internal error, unable to parse password response");
                    }
                }
                fillLoginPage(model);
                return "login";
            }
            model.put("forgotPasswordResponse", "Link validation failed, incorrect username");
            fillLoginPage(model);
            return "login";
        } catch (IOException e) {
            logger.error("Internal error, unable to get change password response", e);
            model.put("forgotPasswordResponse", "Internal error, unable to get change password response");
            fillLoginPage(model);
            return "login";
        }
    }

    private Msg wrapIntoMsg(UserOpUpdateRequest.Builder requestMessage) {
        return Msg.newBuilder().setUserOperation(newBuilder().setUserOpUpdateRequest(requestMessage))
                .build();
    }

    private Msg wrapIntoMsg(UserOpValidatePasswordLink message) {
        return Msg.newBuilder()
                .setUserOperation(
                        newBuilder()
                                .setUserOpValidatePasswordLink(message)
                ).build();
    }

    private Msg wrapIntoMsg(UserOpChangePassword.Builder requestMessage) {
        return Msg.newBuilder().setUserOperation(newBuilder().setUserOpChangePassword(requestMessage))
                .build();
    }

    private void configurePersonalInfo(ModelMap model, String username, User user, Response response) throws IOException {
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
            model.put("error", "Error received from backend, unable to get search result");
        }
    }

    private void showPersonalInfo(Request request, ModelMap model, User user) {
        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());

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

    private void fillLoginPage(ModelMap model) {
        model.put("genders", Msg.UserOp.Gender.values());
        model.put("createRequest", UserOpCreateRequest.newBuilder());
    }
}
