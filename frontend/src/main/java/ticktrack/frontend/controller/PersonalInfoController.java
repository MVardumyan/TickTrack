package ticktrack.frontend.controller;

import com.google.protobuf.util.JsonFormat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ticktrack.proto.Msg;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class PersonalInfoController {
    private final OkHttpClient httpClient;

    @Autowired
    public PersonalInfoController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/personalInfo", method = RequestMethod.GET)
    public String displayPersonalInfoPage(ModelMap model, HttpSession httpSession) {
        Request request = new Request.Builder()
                .url("http://localhost:9001/backend/v1/users/getUser/" + httpSession.getAttribute("name"))
                .build();
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
        return "personalInfo";
    }
}
