package ticktrack.frontend.controller;

import com.google.protobuf.util.JsonFormat;
import common.helpers.CustomJsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ticktrack.proto.Msg;

import java.io.IOException;

import static common.helpers.CustomJsonParser.jsonToProtobuf;

@Controller
public class TicketInfoController {
    private final OkHttpClient httpClient;

    private final Logger logger = LoggerFactory.getLogger(TicketInfoController.class);
    private String backendURL = "http://localhost:9001/backend/v1/";

    @Autowired
    public TicketInfoController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/ticketInfo/{id}", method = RequestMethod.GET)
    public String displayTicketInfoPage(ModelMap model,@PathVariable("id") long id) {
        Request request = new Request.Builder()
                .url(backendURL + "Tickets/getTicket/" + id )
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() == 200) {
                    Msg msg = jsonToProtobuf(response.body().string());
                    if (msg != null) {
                        model.put("info", msg.getTicketInfo());
                    }
                } else {
                    logger.warn("Error received from backend, unable to get search result: {}", response.message());
                }
            } catch (IOException e) {
                logger.error("Internal error, unable to get users list", e);
            }

        return "ticketInfo";
    }

    private Msg wrapIntoMsg(Msg.TicketInfo.Builder requestMessage) {
        return Msg.newBuilder()
             .setTicketInfo(Msg.TicketInfo.newBuilder().setTicketID(requestMessage.getTicketID()))
                .build();
    }
    @RequestMapping(value = "/ticketInfoWithResolve", method = RequestMethod.GET)
    public String displayTicketInfoWithResolvePage(ModelMap model) {
        Request request = new Request.Builder()
                .url("http://localhost:9001/backend/v1/Tickets/getTicket/19")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            Msg.Builder builder = Msg.newBuilder();
            JsonFormat.parser().merge(response.body().string(), builder);
            Msg result = builder.build();

            model.put("id", result.getTicketInfo().getTicketID());
            model.put("creator", result.getTicketInfo().getCreator());
            model.put("description", result.getTicketInfo().getDescription());
            model.put("summary", result.getTicketInfo().getSummary());
            model.put("resolution", result.getTicketInfo().getResolution());
            model.put("priority", result.getTicketInfo().getPriority());
            model.put("status", result.getTicketInfo().getStatus());
            model.put("category", result.getTicketInfo().getCategory());
            model.put("assignee", result.getTicketInfo().getAssignee());
            model.put("deadline", result.getTicketInfo().getDeadline());
            model.put("date", result.getTicketInfo().getCloseDate());
            model.put("openDate", result.getTicketInfo().getOpenDate());
            model.put("group", result.getTicketInfo().getGroup());
            model.put("comments", result.getTicketInfo().getCommentList());


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ticketInfoWithResolve";
    }
}
