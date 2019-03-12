package ticktrack.frontend.controller;

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
    public String displayTicketInfoPage(ModelMap model, @PathVariable("id") long id,@SessionAttribute User user) {

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "Tickets/getTicket/" + id)).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    if(user.getRole().equals(UserRole.BusinessUser)){
                        model.put("resolve",true);
                    }
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
    public String displayUpdateTicketPage(ModelMap model, @PathVariable("id") long id) {
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
    String updateTicket(ModelMap model, @PathVariable("id") long id,
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
        if (assignee != null) {
            requestMessage.setAssignee(assignee);
        }
        if (group != null) {
            requestMessage.setGroup(group);
        }
//        if(deadline != null && deadline.length() > 0){
//            requestMessage.setDeadline(()deadline);
//        }

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    if(user.getRole().equals(UserRole.BusinessUser)){
                        model.put("resolve",true);
                    }
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

    @RequestMapping(value = "/closeTicket/{id}", method = RequestMethod.GET)
    String displayCloseTicket(ModelMap model, @PathVariable("id") long id) {
        model.put("id", id);
        model.put("close",true);
        model.put("cancel",false);
        model.put("progress",false);
        return "message";
    }

    @RequestMapping(value = "closeTicket/closeTheTicket/{id}", method = RequestMethod.POST)
    String closeTicket(ModelMap model, @PathVariable("id") long id,@SessionAttribute User user) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();
        requestMessage.setTicketID(id).setStatus(Msg.TicketStatus.Closed);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    if(user.getRole().equals(UserRole.BusinessUser)){
                        model.put("resolve",true);
                    }
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

    @RequestMapping(value = "/progressTicket/{id}", method = RequestMethod.GET)
    String displayInProgressTicket(ModelMap model, @PathVariable("id") long id) {
        model.put("id", id);
        model.put("close",false);
        model.put("cancel",false);
        model.put("progress",true);
        return "message";
    }

    @RequestMapping(value = "progressTicket/progressTheTicket/{id}", method = RequestMethod.POST)
    String inProgressTicket(ModelMap model, @PathVariable("id") long id,@SessionAttribute User user) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();
        requestMessage.setTicketID(id).setStatus(Msg.TicketStatus.InProgress);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    if(user.getRole().equals(UserRole.BusinessUser)){
                        model.put("resolve",true);
                    }
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

    @RequestMapping(value = "/cancelTicket/{id}", method = RequestMethod.GET)
    String displayCancelTicket(ModelMap model, @PathVariable("id") long id) {
        model.put("id", id);
        model.put("close",false);
        model.put("cancel",true);
        model.put("progress",false);
        return "message";
    }

    @RequestMapping(value = "cancelTicket/cancelTheTicket/{id}", method = RequestMethod.POST)
    String cancelTicket(ModelMap model, @PathVariable("id") long id,@SessionAttribute User user) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();
        requestMessage.setTicketID(id).setStatus(Msg.TicketStatus.Canceled);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    if(user.getRole().equals(UserRole.BusinessUser)){
                        model.put("resolve",true);
                    }
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


    @RequestMapping(value = "/resolveTicket/{id}", method = RequestMethod.GET)
    String displayResolveTicket(ModelMap model, @PathVariable("id") long id) {
        model.put("id", id);
        return "resolveTicket";
    }

    @RequestMapping(value = "resolveTicket/resolveTheTicket/{id}", method = RequestMethod.POST)
    String resolveTicket(ModelMap model, @PathVariable("id") long id,@RequestParam(required = false) String resolution) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();
        requestMessage.setTicketID(id).setResolution(resolution).setStatus(Msg.TicketStatus.Resolved);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("info", msg.getTicketInfo());
                    model.put("id", id);
                    model.put("resolve",true);
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        return "ticketInfo";
    }

}
