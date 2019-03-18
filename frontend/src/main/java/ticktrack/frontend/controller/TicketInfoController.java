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
import ticktrack.proto.Msg.CategoryOp.CategoryOpGetAllResponse.CategoryInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static common.helpers.CustomJsonParser.jsonToProtobuf;

@Controller
public class TicketInfoController {
    private final OkHttpClient httpClient;

    private final Logger logger = LoggerFactory.getLogger(TicketInfoController.class);
    private String backendURL = "http://localhost:9201/backend/v1/";

    @Autowired
    public TicketInfoController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/ticketInfo/{id}", method = RequestMethod.GET)
    public String displayTicketInfoPage(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "Tickets/getTicket/" + id)).execute()) {
            displayTicketConfig(model, id, user, response);
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }

        return "ticketInfo";
    }

    private void displayTicketConfig(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user, Response response) throws IOException {
        if (response.code() == 200) {
            Msg msg = jsonToProtobuf(response.body().string());
            if (msg != null) {
                if (user.getRole().equals(UserRole.BusinessUser)) {
                    model.put("resolve", true);
                } else if (user.getRole().equals(UserRole.Admin)) {
                    model.put("admin", true);
                }
                model.put("info", msg.getTicketInfo());
                model.put("id", id);

                List<Msg.Comment> commentList = new ArrayList<Msg.Comment>(msg.getTicketInfo().getCommentList());
                commentList.sort((Comparator<Msg.Comment>) (o1, o2) -> {
                    if (o1.getTime() == null || o2.getTime() == null)
                        return 0;
                    return o1.getTime().compareTo(o2.getTime());
                });

                model.put("commentList", commentList);
                if (!msg.getTicketInfo().getStatus().equals(Msg.TicketStatus.Closed)) {
                    model.put("notClosed", true);
                }
            }
        } else {
            logger.warn("Error received from backend, unable to get search result: {}", response.message());
            model.put("error", "Error received from backend, unable to get search result");
        }
    }

    @RequestMapping(value = "/addComment/{id}", method = RequestMethod.POST)
    String addComment(ModelMap model, @PathVariable("id") long id,
                      @SessionAttribute("user") User user,
                      @RequestParam() String comment) {

        Msg.Comment commentObj = Msg.Comment.newBuilder()
                .setUsername(user.getUsername())
                .setText(comment)
                .build();

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/addComment", CustomJsonParser.protobufToJson(wrapCommentIntoMsg(commentObj, id)))
        ).execute()) {
            displayTicketConfig(model, id, user, response);
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }

        return "redirect:/ticketInfo/" + id;
    }

    private Msg wrapCommentIntoMsg(Msg.Comment comment, long id) {
        return Msg.newBuilder()
                .setTicketOperation(Msg.TicketOp.newBuilder()
                        .setTicketOpAddComment(Msg.TicketOp.TicketOpAddComment.newBuilder()
                                .setNewComment(comment)
                                .setTicketId(id)
                        )
                ).build();
    }


    @RequestMapping(value = "/updateTicket/{id}", method = RequestMethod.GET)
    public String displayUpdateTicketPage(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {
        Request requestCategory = new Request.Builder()
                .url(backendURL + "categories/getAll")
                .build();
        Request groupsRequest = new Request.Builder()
                .url(backendURL + "userGroups/getAll")
                .build();
        if (user.getRole().equals(UserRole.Admin)) {
            model.put("admin", true);
        }
        categoryResponseUtil(model, requestCategory, groupsRequest, httpClient, logger);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithoutBody(backendURL + "Tickets/getTicket/" + id)).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("info", msg.getTicketInfo());
                    model.put("id", id);
                    model.put("commentList", msg.getTicketInfo().getCommentList());
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
                model.put("error", "Error received from backend, unable to get search result");
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }

        return "updateTicketInfo";
    }

    static void categoryResponseUtil(ModelMap model, Request requestCategory, Request groupsRequest, OkHttpClient httpClient, Logger logger) {
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
                }
            } else {
                logger.warn("Error received from backend, unable to get categories list: {}", categoryResponse.message());
                model.put("error", "Error received from backend, unable to get categories list");
            }

            groupListResultUtil(model, logger, groupResponse);
        } catch (IOException e) {
            logger.error("Internal error, unable to get categories list", e);
            model.put("error", "Internal error, unable to get categories list");
        }
    }

    static void groupListResultUtil(ModelMap model, Logger logger, Response groupResponse) throws IOException {
        if (groupResponse.code() == 200) {
            Msg result = jsonToProtobuf(groupResponse.body().string());

            if (result != null) {
                model.put("groupList", result.getUserGroupOperation().getUserGroupOpGetAllResponse().getGroupNameList());
            }
        } else {
            logger.warn("Error received from backend, unable to get group list: {}", groupResponse.message());
        }
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
        if (!"".equals(deadline)) {
            requestMessage.setDeadline(deadline);
        }

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    if (user.getRole().equals(UserRole.BusinessUser)) {
                        model.put("resolve", true);
                    } else if (user.getRole().equals(UserRole.Admin)) {
                        model.put("admin", true);
                    }
                    model.put("info", msg.getTicketInfo());
                    model.put("id", id);
                    model.put("commentList", msg.getTicketInfo().getCommentList());
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
                model.put("error", "Error received from backend, unable to get search result");
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
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
    String displayCloseTicket(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {
        model.put("id", id);
        model.put("close", true);
        model.put("cancel", false);
        model.put("progress", false);
        if (user.getRole().equals(UserRole.Admin)) {
            model.put("admin", true);
        }
        return "message";
    }

    @RequestMapping(value = "closeTicket/closeTheTicket/{id}", method = RequestMethod.POST)
    String closeTicket(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();
        requestMessage.setTicketID(id).setStatus(Msg.TicketStatus.Closed);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    if (user.getRole().equals(UserRole.BusinessUser)) {
                        model.put("resolve", true);
                    } else if (user.getRole().equals(UserRole.Admin)) {
                        model.put("admin", true);
                    }
                    model.put("info", msg.getTicketInfo());
                    model.put("id", id);
                    model.put("commentList", msg.getTicketInfo().getCommentList());
                    model.put("notClosed", false);
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
                model.put("error", "Error received from backend, unable to get search result");
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }

        return "ticketInfo";
    }

    @RequestMapping(value = "/progressTicket/{id}", method = RequestMethod.GET)
    String displayInProgressTicket(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {
        model.put("id", id);
        model.put("close", false);
        model.put("cancel", false);
        model.put("progress", true);
        if (user.getRole().equals(UserRole.Admin)) {
            model.put("admin", true);
        }
        return "message";
    }

    @RequestMapping(value = "progressTicket/progressTheTicket/{id}", method = RequestMethod.POST)
    String inProgressTicket(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();
        requestMessage.setTicketID(id).setStatus(Msg.TicketStatus.InProgress);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            displayTicketConfig(model, id, user, response);
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }

        return "ticketInfo";
    }

    @RequestMapping(value = "/cancelTicket/{id}", method = RequestMethod.GET)
    String displayCancelTicket(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {
        model.put("id", id);
        model.put("close", false);
        model.put("cancel", true);
        model.put("progress", false);
        if (user.getRole().equals(UserRole.Admin)) {
            model.put("admin", true);
        }
        return "message";
    }

    @RequestMapping(value = "cancelTicket/cancelTheTicket/{id}", method = RequestMethod.POST)
    String cancelTicket(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();
        requestMessage.setTicketID(id).setStatus(Msg.TicketStatus.Canceled);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            displayTicketConfig(model, id, user, response);
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }

        return "ticketInfo";
    }


    @RequestMapping(value = "/resolveTicket/{id}", method = RequestMethod.GET)
    String displayResolveTicket(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user) {
        model.put("id", id);
        return "resolveTicket";
    }

    @RequestMapping(value = "resolveTicket/resolveTheTicket/{id}", method = RequestMethod.POST)
    String resolveTicket(ModelMap model, @PathVariable("id") long id, @SessionAttribute User user, @RequestParam(required = false) String resolution) {

        Msg.TicketOp.TicketOpUpdateRequest.Builder requestMessage = Msg.TicketOp.TicketOpUpdateRequest.newBuilder();
        requestMessage.setTicketID(id).setResolution(resolution).setStatus(Msg.TicketStatus.Resolved);

        try (Response response = httpClient.newCall(OkHttpRequestHandler.buildRequestWithBody(backendURL + "Tickets/update", CustomJsonParser.protobufToJson(wrapIntoMsg(requestMessage)))
        ).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("info", msg.getTicketInfo());
                    model.put("id", id);
                    model.put("resolve", true);
                    model.put("commentList", msg.getTicketInfo().getCommentList());
                    if (user.getRole().equals(UserRole.Admin)) {
                        model.put("admin", true);
                    }
                    if (!msg.getTicketInfo().getStatus().equals(Msg.TicketStatus.Closed)) {
                        model.put("notClosed", true);
                    }
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
                model.put("error", "Error received from backend, unable to get search result");
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            model.put("error", "Internal error, unable to get users list");
        }

        return "ticketInfo";
    }

}
