package ticktrack.frontend.controller;

import common.enums.UserRole;
import common.helpers.PasswordHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ticktrack.frontend.attributes.User;
import ticktrack.proto.Msg;
import ticktrack.proto.Msg.UserOp.UserOpCreateRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static common.enums.UserRole.*;
import static common.helpers.CustomJsonParser.*;
import static ticktrack.frontend.util.CaptchaValidation.isCaptchaValid;
import static ticktrack.frontend.util.OkHttpRequestHandler.*;
import static ticktrack.proto.Msg.CommonResponse.ResponseType.Success;

import ticktrack.frontend.util.CaptchaValidation;

/**
 * This controller is responsible for logout, login and register pages displaying and functionality.
 * In this page is supported reCaptcha.
 */

@Controller
class SessionController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(SessionController.class);
    private String backendURL = "http://localhost:9201/backend/v1/";

    @Autowired
    public SessionController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    String showLoginPage(ModelMap model) {
        model.put("failure", false);
        model.put("logout", false);
        model.put("registerFailure", false);
        fillLoginPage(model);
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    String login(ModelMap model, HttpServletRequest _request,
                 @RequestParam(name = "g-recaptcha-response") String recaptchaResponse,
                 HttpSession httpSession, @RequestParam String username, @RequestParam String password) {

        if (isCaptchaValid("6LeMC5gUAAAAAKqvqDtTyuY-q3u5YXSHRw-7QiNH", _request.getParameter("g-recaptcha-response"))) {
            //valid
            Msg requestMessage = buildLoginValidationRequest(username, PasswordHandler.encode(password));

            Request request = buildRequestWithBody(backendURL + "users/validateLogin",
                    protobufToJson(requestMessage));

            try (Response response = httpClient.newCall(request).execute()) {
                Msg result = jsonToProtobuf(response.body().string());
                if (result != null) {
                    if (result.getCommonResponse().getResponseType().equals(Success)) {
                        Request roleRequest = buildRequestWithoutBody(backendURL + "users/getUser/" + username);
                        Response roleResponse = httpClient.newCall(roleRequest).execute();
                        Msg roleResult = jsonToProtobuf(roleResponse.body().string());

                        if (roleResult != null && roleResult.getUserOperation().getUserOpGetResponse().getUserInfoCount() == 1) {
                            User user = new User(username,
                                    valueOf(roleResult.getUserOperation().getUserOpGetResponse().getUserInfo(0).getRole().name())
                            );

                            user.setUserGroup(roleResult.getUserOperation().getUserOpGetResponse().getUserInfo(0).getGroup());

                            httpSession.setAttribute("user", user);

                            model.put("name", username);
                            if (Admin.equals(user.getRole())) {
                                model.put("admin", true);
                                return "adminMain";
                            } else {
                                model.put("notRegular", true);
                                return "regularUserMain";
                            }
                        } else {
                            model.put("error", "User not found");
                            return "error";
                        }
                    } else {
                        model.put("failure", true);
                        model.put("logout", false);
                        model.put("registerFailure", false);
                        fillLoginPage(model);
                        return "login";
                    }
                } else {
                    model.put("error", "User not found");
                }
            } catch (IOException e) {
                logger.error("Internal error, unable to get login validation", e);
                model.put("error", "reCAPTCHA is not valid");
            }
        }
        return "error";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    String register(@ModelAttribute UserOpCreateRequest.Builder createRequest, ModelMap model, HttpServletRequest _request,
                    @RequestParam(name = "g-recaptcha-response") String recaptchaResponse, HttpSession session, BindingResult bindingResult) {
        if (isCaptchaValid("6LeMC5gUAAAAAKqvqDtTyuY-q3u5YXSHRw-7QiNH", _request.getParameter("g-recaptcha-response"))) {
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error -> {
                    logger.error("{} : {}", error.getField(), error.getDefaultMessage());
                });
                model.put("error", "binding result has errors");
                return "error";
            }

            createRequest.setRole(Msg.UserRole.RegularUser);
            Request request = buildRequestWithBody(backendURL + "users/add",
                    protobufToJson(buildCreateUserRequest(createRequest)));

            try (Response response = httpClient.newCall(request).execute()) {
                Msg result = jsonToProtobuf(response.body().string());
                if (result != null && result.hasCommonResponse()) {
                    Msg.CommonResponse commonResponse = result.getCommonResponse();
                    if (commonResponse.getResponseType().equals(Success)) {
                        User user = new User(createRequest.getUsername(), RegularUser);
                        session.setAttribute("user", user);

                        if (Admin.equals(user.getRole())) {
                            return "admin/main";
                        } else {
                            model.put("name", createRequest.getUsername());
                            return "regularUserMain";
                        }
                    } else {
                        model.put("registerFailure", true);
                        fillLoginPage(model);
                        return "login";
                    }

                } else {
                    model.put("error", "not valid registration");
                    return "error";
                }
            } catch (IOException e) {
                logger.error("Internal error, unable to create new user", e);
                model.put("error", "Internal error, unable to create new user");
                return "error";
            }
        } else {
            model.put("error", "reCAPTCHA is not valid");
            return "error";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    String logout(ModelMap model, SessionStatus sessionStatus, HttpSession httpSession) {
        sessionStatus.setComplete();
        httpSession.removeAttribute("user");
        model.put("logout", true);
        model.put("failure", false);
        model.put("registerFailure", false);
        fillLoginPage(model);
        return "login";
    }

    private Msg buildLoginValidationRequest(String username, String password) {
        return Msg.newBuilder()
                .setLoginRequest(
                        Msg.LoginRequest.newBuilder()
                                .setUsername(username)
                                .setPassword(password)
                ).build();
    }

    private Msg buildCreateUserRequest(UserOpCreateRequest.Builder createRequest) {
        return Msg.newBuilder()
                .setUserOperation(
                        Msg.UserOp.newBuilder()
                                .setUserOpCreateRequest(createRequest)
                ).build();
    }

    private void fillLoginPage(ModelMap model) {
        model.put("genders", Msg.UserOp.Gender.values());
        model.put("createRequest", UserOpCreateRequest.newBuilder());
    }

}

