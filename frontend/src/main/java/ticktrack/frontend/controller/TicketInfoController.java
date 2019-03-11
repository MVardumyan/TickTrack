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
import org.springframework.web.bind.annotation.*;
import ticktrack.frontend.attributes.User;
import ticktrack.frontend.util.OkHttpRequestHandler;
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

            try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "Tickets/getTicket/" + id)).execute()) {
                if (response.code() == 200) {
                    Msg msg = jsonToProtobuf(response.body().string());
                    if (msg != null) {
                        model.put("info", msg.getTicketInfo());
                        model.put("id", id);
                    }
                } else {
                    logger.warn("Error received from backend, unable to get search result: {}", response.message());
                }
            } catch (IOException e) {
                logger.error("Internal error, unable to get users list", e);
            }

        return "ticketInfo";
    }

    @RequestMapping(value = "/updateTicket/{id}", method = RequestMethod.GET)
    public String displayUpdateTicketPage(ModelMap model,@PathVariable("id") long id) {
        Request requestCategory = new Request.Builder()
                .url("http://localhost:9001/backend/v1/categories/getAll")
                .build();
        Request groupsRequest = new Request.Builder()
                .url(backendURL + "userGroups/getAll")
                .build();
        try (Response categoryResponse = httpClient.newCall(requestCategory).execute();
             Response groupResponse = httpClient.newCall(groupsRequest).execute()) {
            if (categoryResponse.code() == 200) {
                Msg result = jsonToProtobuf(categoryResponse.body().string());

                if (result != null) {
                    model.put("categoryList", result.getCategoryOperation().getCategoryOpGetAllResponse().getCategoryNameList());
                }
            } else {
                logger.warn("Error received from backend, unable to get categories list: {}", categoryResponse.message());
            }

            if (groupResponse.code() == 200) {
                Msg result = jsonToProtobuf(groupResponse.body().string());

                if (result != null) {
                    model.put("groupList", result.getUserGroupOperation().getUserGroupOpGetAllResponse().getGroupNameList());
                }
            } else {
                logger.warn("Error received from backend, unable to get group list: {}", groupResponse.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get categories list", e);
        }

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "Tickets/getTicket/" + id)).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("info", msg.getTicketInfo());
                    model.put("id", id);
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        return "updateTicketInfo";
    }

    @RequestMapping(value = "updateTicket/updateTheTicket/{id}", method = RequestMethod.POST)
    String updateTicket(ModelMap model,@PathVariable("id") long id,
                        @SessionAttribute("user") User user,
                        @RequestParam() String summary,
                        @RequestParam() String description,
                        @RequestParam(required = false) String assignee,
                        @RequestParam(required = false) String group,
                        @RequestParam() String priority,
                        @RequestParam() String category,
                        @RequestParam(required = false) String deadline

    ) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();

        requestMessage
                .setTicketID(id)
                .setSummary(summary)
                .setDescription(description)
                .setPriority(Msg.TicketPriority.valueOf(priority))
                .setCategory(category);
        if(assignee != null){
            requestMessage.setAssignee(assignee);
        }
        if(group != null){
            requestMessage.setGroup(group);
        }
//        if(deadline != null && deadline.length() > 0){
//            requestMessage.setDeadline(()deadline);
//        }

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update",CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("info", msg.getTicketInfo());
                    model.put("id", id);
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        return "ticketInfo";
    }

    private Msg wrapIntoMsg(Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage) {
        return Msg.newBuilder().setTicketOperation(
                Msg.TicketOp.newBuilder().setTicketOpUpdateRequest(requestMessage)
        )
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
