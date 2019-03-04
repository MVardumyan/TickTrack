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

import java.io.IOException;

@Controller
public class PersonalInfoController {
    private final OkHttpClient httpClient;

    @Autowired
    public PersonalInfoController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/personalInfo", method = RequestMethod.GET)
    public String displayPersonalInfoPage(ModelMap model) {
        Request request = new Request.Builder()
                .url("http://localhost:9001/backend/v1/users/getUser/Lusine")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            Msg.Builder builder = Msg.newBuilder();
            JsonFormat.parser().merge(response.body().string(), builder);
            Msg result = builder.build();

            //model.put("request", result.getUserOperation().getUserOpGetResponse());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "personalInfo";
    }
}
