package ticktrack.frontend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
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

import java.io.IOException;
import java.util.List;

@Controller
public class SearchController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    public SearchController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String displaySearchPage(ModelMap model) {
        Request categoriesRequest = new Request.Builder()
                .url("http://localhost:9001/backend/v1/categories/getAllActive")
                .build();

        try (Response response = httpClient.newCall(categoriesRequest).execute()) {
            if(response.code()==200) {
                Msg.Builder builder = Msg.newBuilder();
                JsonFormat.parser().merge(response.body().string(), builder);
                Msg result = builder.build();

                model.put("categoryList", result.getCategoryOperation().getCategoryOpGetAllResponse().getCategoryNameList());
            } else {
                logger.warn("Error received from backend, unable to get categories list: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get categories list", e);
        }

        return "searchTicket";
    }

    @RequestMapping(value = "searchUsers", method = RequestMethod.GET)
    @ResponseBody
    List<String> searchUsers(@RequestParam("term") String term) {
        Request usersRequest = new Request.Builder()
                .url("http://localhost:9001/backend/v1/searchUsers/" + term)
                .build();

        try (Response response = httpClient.newCall(usersRequest).execute()) {
            if(response.code()==200) {
                ObjectMapper mapper = new ObjectMapper();

                return mapper.readValue(response.body().string(), mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } else {
                logger.warn("Error received from backend, unable to get users list: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }
        return null;
    }

}
