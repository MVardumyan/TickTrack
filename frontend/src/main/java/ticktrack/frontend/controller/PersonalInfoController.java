package ticktrack.frontend.controller;
import com.google.protobuf.util.JsonFormat;
import common.helpers.CustomJsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import ticktrack.frontend.attributes.User;
import ticktrack.frontend.util.OkHttpRequestHandler;
import ticktrack.proto.Msg;
import java.io.IOException;

@Controller
public class PersonalInfoController {
    private final OkHttpClient httpClient;
    private String backendURL = "http://localhost:9001/backend/v1/";

    @Autowired
    public PersonalInfoController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/personalInfo", method = RequestMethod.GET)
    public String displayPersonalInfoPage(ModelMap model, @SessionAttribute User user) {

        Request request = OkHttpRequestHandler.buildRequestWithoutBody("http://localhost:9001/backend/v1/users/getUser/" + user.getUsername());
        showPersonalInfo(request,model);
        return "personalInfo";
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.GET)
    String displayUpdateUserInfo(ModelMap model, @SessionAttribute("user") User user) {
        Request request = OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "users/getUser" + user.getUsername());
        try (Response response = httpClient.newCall(request).execute()) {
            Msg.Builder builder = Msg.newBuilder();
            JsonFormat.parser().merge(response.body().string(), builder);
            Msg result = builder.build();
            model.put("firstName", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getFirstname());
            model.put("lastName", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getLastname());
            model.put("gender", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getGender());
            model.put("email", result.getUserOperation().getUserOpGetResponse().getUserInfo(0).getEmail());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "updateUserInfo";
    }

    @RequestMapping(value = "updateUsersInfo", method = RequestMethod.POST)
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

        OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/update",CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)));


        Request request = OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "/users/getUser/" + user.getUsername());
        showPersonalInfo(request,model);
        return "personalInfo";
    }

    private Msg wrapIntoMsg(Msg.UserOp.UserOpUpdateRequest.Builder requestMessage) {
        return Msg.newBuilder().setUserOperation(Msg.UserOp.newBuilder().setUserOpUpdateRequest(requestMessage))
                .build();
    }

    //CHANGE PASSWORD

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    String displayChangePassword(ModelMap model, @SessionAttribute("user") User user) {

        return "changePassword";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    String changePassword(ModelMap model, @SessionAttribute("user") User user,
                          @RequestParam() String oldPassword,
                          @RequestParam() String newPassword) {

        Msg.UserOp.UserOpChangePassword.Builder requestMessage = Msg.UserOp.UserOpChangePassword.newBuilder();
        requestMessage.setUsername(user.getUsername())
                .setOldPassword(oldPassword)
                .setNewPassword(newPassword);

        if(oldPassword!=null ){
            requestMessage.setNewPassword(newPassword);
        }else {
            return "error";
        }

        OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/changePassword",CustomJsonParser.protobufToJson(wrapPasswordIntoMsg(requestMessage)));


        Request request = OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "users/getUser/");
        showPersonalInfo(request,model);
        return "personalInfo";
    }

    private Msg wrapPasswordIntoMsg(Msg.UserOp.UserOpChangePassword.Builder requestMessage) {
        return Msg.newBuilder().setUserOperation(Msg.UserOp.newBuilder().setUserOpChangePassword(requestMessage))
                .build();
    }

    void showPersonalInfo(Request request,ModelMap model){
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
