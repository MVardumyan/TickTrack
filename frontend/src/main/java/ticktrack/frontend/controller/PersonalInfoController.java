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
import java.io.IOException;

import static common.helpers.CustomJsonParser.jsonToProtobuf;

@Controller
public class PersonalInfoController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(NewTicketController.class);
    private String backendURL = "http://localhost:9001/backend/v1/";

    @Autowired
    public PersonalInfoController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/personalInfo", method = RequestMethod.GET)
    public String displayPersonalInfoPage(ModelMap model, @SessionAttribute User user) {
        if(!user.getRole().equals(UserRole.RegularUser)) {
            model.put("notRegular", true);
        }
        Request request = OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "users/getUser/" + user.getUsername());
        showPersonalInfo(request,model);
        return "personalInfo";
    }

    @RequestMapping(value = "/updateUserInfo/", method = RequestMethod.GET)
    String displayUpdateUserInfo(ModelMap model, @SessionAttribute("user") User user) {

        return "redirect:/updateUserInfo/"+user.getUsername();
    }

    @RequestMapping(value = "/updateUserInfo/{username}", method = RequestMethod.GET)
    String displayUpdateUserInfo(ModelMap model, @PathVariable("username") String username, @SessionAttribute("user") User user) {
        if(user.getUsername().equals(username) || user.getRole().equals(UserRole.Admin)) {

            Request request = OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "users/getUser/" + username);
            try (Response response = httpClient.newCall(request).execute()) {
                Msg result = jsonToProtobuf(response.body().string());
                model.put("firstName", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getFirstname());
                model.put("lastName", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getLastname());
                model.put("gender", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getGender());
                model.put("email", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getEmail());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "updateUserInfo";
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/updateUsersInfo", method = RequestMethod.POST)
    String updateUserInfo(ModelMap model, @SessionAttribute("user") User user,
                          @RequestParam() String firstName,
                          @RequestParam() String lastName,
                          @RequestParam() String email
                          ) {

        Msg.UserOp.UserOpUpdateRequest.Builder requestMessage = Msg.UserOp.UserOpUpdateRequest.newBuilder();
        requestMessage.setUsername(user.getUsername())
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email);
//        if (gender!=null){
//            requestMessage.setGender(Msg.UserOp.Gender.valueOf(gender));
//        }


        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/update",CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    if(!user.getRole().equals(UserRole.RegularUser)) {
                        model.put("notRegular", true);
                    }
                    Request request = OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "users/getUser/" + user.getUsername());
                    showPersonalInfo(request,model);
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }



        return "personalInfo";
    }

    private Msg wrapIntoMsg(Msg.UserOp.UserOpUpdateRequest.Builder requestMessage) {
        return Msg.newBuilder().setUserOperation(Msg.UserOp.newBuilder().setUserOpUpdateRequest(requestMessage))
                .build();
    }

    /////////////////////////////////////CHANGE PASSWORD

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    String displayChangePassword(ModelMap model, @SessionAttribute("user") User user) {

        return "changePassword";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    String changePassword(ModelMap model, @SessionAttribute("user") User user,
                          @RequestParam() String oldPassword,
                          @RequestParam() String newPassword) {

        Msg.UserOp.UserOpChangePassword.Builder requestMessage = Msg.UserOp.UserOpChangePassword.newBuilder();
        requestMessage.setUsername(user.getUsername());

        if(oldPassword!=null ){
            requestMessage.setOldPassword(oldPassword);
            requestMessage.setNewPassword(newPassword);
        }else {
            return "error";
        }

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/changePassword",CustomJsonParser.protobufToJson(wrapPasswordIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    Request request = OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "users/getUser/" + user.getUsername());
                    showPersonalInfo(request,model);
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        return "personalInfo";
    }

    private Msg wrapPasswordIntoMsg(Msg.UserOp.UserOpChangePassword.Builder requestMessage) {
        return Msg.newBuilder().setUserOperation(Msg.UserOp.newBuilder().setUserOpChangePassword(requestMessage))
                .build();
    }

    private void showPersonalInfo(Request request, ModelMap model){
        try (Response response = httpClient.newCall(request).execute()) {
            Msg.Builder builder = Msg.newBuilder();
            JsonFormat.parser().merge(response.body().string(), builder);
            Msg result = builder.build();
            model.put("username", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getUsername()); //username
            model.put("role", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getRole()); //role
            model.put("firstName", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getFirstname());
            model.put("lastName", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getLastname());
            model.put("gender", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getGender());
            model.put("email", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getEmail());
            model.put("regTime", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getRegistrationTime());
            model.put("groups", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getGroup());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
