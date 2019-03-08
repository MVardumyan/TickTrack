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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ticktrack.frontend.attributes.User;
import ticktrack.proto.Msg;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static common.helpers.CustomJsonParser.*;
import static ticktrack.frontend.util.OkHttpRequestHandler.*;

@Controller
class SessionController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(SessionController.class);
    private String backendURL = "http://localhost:9001/backend/v1/";

    @Autowired
    public SessionController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    String showLoginPage(ModelMap model) {
        model.put("failure", false);
        model.put("logout", false);
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    String login(ModelMap model, HttpSession httpSession, @RequestParam String username, @RequestParam String password) {
        Msg requestMessage = buildLoginValidationRequest(username, PasswordHandler.encode(password));

        Request request = buildRequestWithBody(backendURL + "users/validateLogin",
                protobufToJson(requestMessage));

        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());
            if (result != null) {
                if (result.getCommonResponse().getResponseType().equals(Msg.CommonResponse.ResponseType.Success)) {
                    Request roleRequest = buildRequestWithoutBody(backendURL + "users/getUser/" + username);

                    Response roleResponse = httpClient.newCall(roleRequest).execute();
                    Msg roleResult = jsonToProtobuf(roleResponse.body().string());

                    if(roleResult!=null && roleResult.getUserOperation().getUserOpGetResponse().getUserInfoCount()==1) {
                        User user = new User(username,
                                UserRole.valueOf(roleResult.getUserOperation().getUserOpGetResponse().getUserInfo(0).getRole().name())
                                );

                        httpSession.setAttribute("user", user);
                        model.put("name", username);
                        return "regularUserMain";
                    } else {
                        return "error";
                    }

                } else {
                    model.put("failure", true);
                    model.put("logout", false);
                    return "login";
                }
            } else {
                return "error";
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get login validation", e);
            return "error";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    String logout(ModelMap model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        model.put("logout", true);
        model.put("failure", false);
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

}

