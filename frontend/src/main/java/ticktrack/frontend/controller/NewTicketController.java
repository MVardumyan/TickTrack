package ticktrack.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import common.enums.UserRole;
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
import ticktrack.proto.Msg;
import ticktrack.proto.Msg.CategoryOp.CategoryOpGetAllResponse.CategoryInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static common.helpers.CustomJsonParser.jsonToProtobuf;
import static ticktrack.frontend.util.OkHttpRequestHandler.buildRequestWithoutBody;

@Controller
public class NewTicketController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(NewTicketController.class);
    private String backendURL = "http://localhost:9201/backend/v1/";

    @Autowired
    public NewTicketController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/newTicket", method = RequestMethod.GET)
    public String displayNewTicketPage(ModelMap model,@SessionAttribute User user) {
        Request requestCategory = new Request.Builder()
                .url(backendURL + "categories/getAll")
                .build();
        Request groupsRequest = new Request.Builder()
                .url(backendURL + "userGroups/getAll")
                .build();
        try (Response categoryResponse = httpClient.newCall(requestCategory).execute();
             Response groupResponse = httpClient.newCall(groupsRequest).execute()) {
            if (categoryResponse.code() == 200) {
                Msg result = jsonToProtobuf(categoryResponse.body().string());

                if (result != null) {
                    model.put("categoryList", result.getCategoryOperation().getCategoryOpGetAllResponse().getCategoryInfoList()
                            .stream()
                            .filter(categoryInfo -> !categoryInfo.getIsDeactivated())
                            .map(CategoryInfo::getCategoryName)
                            .collect(Collectors.toList()));
                    if(user.getRole().equals(UserRole.Admin)){
                        model.put("admin",true);
                    }
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

        return "newTicket";
    }

    @RequestMapping(value = "createTicket", method = RequestMethod.POST)
    String createTicket(ModelMap model,
                        @SessionAttribute("user") User user,
                         @RequestParam() String summary,
                         @RequestParam() String description,
                         @RequestParam(required = false) String assignee,
                         @RequestParam(required = false) String group,
                         @RequestParam() String priority,
                         @RequestParam() String category,
                         @RequestParam(required = false) String deadline

    ) {

        Msg.TicketOp.TicketOpCreateRequest.Builder requestMessage = Msg.TicketOp.TicketOpCreateRequest.newBuilder();

        requestMessage
                .setCreator(user.getUsername())
                .setSummary(summary)
                .setDescription(description)
                .setPriority(priority)
                .setCategory(category);
        if(assignee != null){
            requestMessage.setAssignee(assignee);
        }
        if(group != null){
            requestMessage.setGroup(group);
        }
        if(!"".equals(deadline)){
            requestMessage.setDeadline(deadline);
        }

        Request request = new Request.Builder()
                .url(backendURL + "Tickets/add")
                .post(
                        okhttp3.RequestBody.create(
                                MediaType.parse("application/json; charset=utf-8"),
                                CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
                )
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("info", msg.getTicketInfo());
                    //msg.getTicketInfo().getTicketID();
                    if(user.getRole().equals(UserRole.Admin)){
                        model.put("admin",true);
                    }
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        return "ticketInfo";
    }

    private Msg wrapIntoMsg(Msg.TicketOp.TicketOpCreateRequest.Builder requestMessage) {
        return Msg.newBuilder().setTicketOperation(
                        Msg.TicketOp.newBuilder().setTicketOpCreateRequest(requestMessage)
                )
                .build();
    }
}
