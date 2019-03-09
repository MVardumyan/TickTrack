package ticktrack.frontend.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import ticktrack.frontend.attributes.User;
import ticktrack.proto.Msg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static common.helpers.CustomJsonParser.jsonToProtobuf;
import static common.helpers.CustomJsonParser.protobufToJson;
import static ticktrack.frontend.util.OkHttpRequestHandler.buildRequestWithBody;

@Controller
public class MyTicketsController {
    private final OkHttpClient httpClient;
    private String backendURL = "http://localhost:9001/backend/v1/";
    private final Logger logger = LoggerFactory.getLogger(MyTicketsController.class);

    @Autowired
    public MyTicketsController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/myTickets", method = RequestMethod.GET)
    String displayMyTickets(ModelMap model,@SessionAttribute("user") User user) {

        Msg.SearchOp.SearchOpRequest.Builder requestCreatedByMe = Msg.SearchOp.SearchOpRequest.newBuilder();
        Msg.SearchOp.SearchOpRequest.Builder requestAssignedToMe = Msg.SearchOp.SearchOpRequest.newBuilder();
        Msg.SearchOp.SearchOpRequest.Builder requestAssignedToMyGroups = Msg.SearchOp.SearchOpRequest.newBuilder();

        requestCreatedByMe.setCreator(user.getUsername());
        requestAssignedToMe.setAssignee(user.getUsername());

        if (user.getUserGroup() != null) {
           requestAssignedToMyGroups.setGroup(user.getUserGroup());
        }

        Request reqCreatedByMe = buildRequestWithBody(backendURL + "search",
                protobufToJson(wrapIntoMsg(requestCreatedByMe)));

        Request reqAssignedToMe = buildRequestWithBody(backendURL + "search",
                protobufToJson(wrapIntoMsg(requestAssignedToMe)));

        Request reqAssignedToMyGroups = buildRequestWithBody(backendURL + "search",
                protobufToJson(wrapIntoMsg(requestAssignedToMyGroups)));

        try (Response response = httpClient.newCall(reqCreatedByMe).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("ticketsCreatedByMe", msg.getSearchOperation().getSearchOpResponse().getTicketInfoList());
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        try (Response response = httpClient.newCall(reqAssignedToMe).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("ticketsAssignedToMe", msg.getSearchOperation().getSearchOpResponse().getTicketInfoList());
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        try (Response response = httpClient.newCall(reqAssignedToMyGroups).execute()) {
            if (response.code() == 200) {
                Msg msg = jsonToProtobuf(response.body().string());
                if (msg != null) {
                    model.put("ticketsAssignedToMyGroup", msg.getSearchOperation().getSearchOpResponse().getTicketInfoList());
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }


        return "myTickets";
    }

    private Msg wrapIntoMsg(Msg.SearchOp.SearchOpRequest.Builder requestMessage) {
        return Msg.newBuilder()
                .setSearchOperation(
                        Msg.SearchOp.newBuilder()
                                .setSearchOpRequest(requestMessage)
                )
                .build();
    }
}
