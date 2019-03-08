package ticktrack.frontend.controller;

import common.helpers.PasswordHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ticktrack.proto.Msg;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static common.helpers.CustomJsonParser.*;

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
    String login(ModelMap model, HttpSession session, @RequestParam String username, @RequestParam String password) {
        Msg requestMessage = buildLoginValidationRequest(username, PasswordHandler.encode(password));

        Request request = new Request.Builder()
                .url(backendURL + "users/validateLogin")
                .post(
                        okhttp3.RequestBody.create(
                                MediaType.parse("application/json; charset=utf-8"),
                                protobufToJson(requestMessage))
                ).build();

        try (Response response = httpClient.newCall(request).execute()) {
            Msg result = jsonToProtobuf(response.body().string());
            if (result != null) {
                if (result.getCommonResponse().getResponseType().equals(Msg.CommonResponse.ResponseType.Success)) {
                    session.setAttribute("name", username);
                    return "regularUserMain";
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
    String logout(ModelMap model, HttpSession httpSession) {
        httpSession.removeAttribute("name");
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

