package ticktrack.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ticktrack.proto.Msg;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static common.helpers.CustomJsonParser.*;

@Controller
public class SearchController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(SearchController.class);
    private String backendURL = "http://localhost:9001/backend/v1/";

    @Autowired
    public SearchController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    String displaySearchPage(ModelMap model) {
        Request categoriesRequest = new Request.Builder()
                .url(backendURL + "categories/getAllActive")
                .build();
        Request groupsRequest = new Request.Builder()
                .url(backendURL + "userGroups/getAll")
                .build();

        try (Response categoryResponse = httpClient.newCall(categoriesRequest).execute();
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

        return "searchTicket";
    }

    @RequestMapping(value = "searchTickets", method = RequestMethod.POST)
    String searchTickets(ModelMap model,
                         @RequestParam(required = false) String summaryOrDescription,
                         @RequestParam(required = false) String ticket_id,
                         @RequestParam(required = false) String assignee,
                         @RequestParam(required = false) String[] group,
                         @RequestParam(required = false) String creator,
                         @RequestParam(required = false) String resolution,
                         @RequestParam(required = false) String[] priority,
                         @RequestParam(required = false) String[] status,
                         @RequestParam(required = false) String[] category,
                         @RequestParam(required = false) String openDateStart,
                         @RequestParam(required = false) String openDateEnd,
                         @RequestParam(required = false) String closeDateStart,
                         @RequestParam(required = false) String closeDateEnd,
                         @RequestParam(required = false) String deadlineStart,
                         @RequestParam(required = false) String deadlineEnd

    ) {

        Msg.SearchOp.SearchOpRequest.Builder requestMessage = Msg.SearchOp.SearchOpRequest.newBuilder();

        if (summaryOrDescription != null) {
            requestMessage.setSummaryOrDescription(summaryOrDescription);
        }

        if (assignee != null) {
            requestMessage.setAssignee(assignee);
        }

        if (creator != null) {
            requestMessage.setCreator(creator);
        }

        if (resolution != null) {
            requestMessage.setResolution(resolution);
        }

        if (group != null && group.length > 0) {
            List<String> groups = Arrays.asList(group);
            requestMessage.addAllCategory(groups);
        }

        if(ticket_id!=null) {
            Long[] ticketIDs = (Long[]) Arrays.stream(ticket_id.split(";")).map(Long::parseLong).toArray();
            requestMessage.addAllTicketId(Arrays.asList(ticketIDs));
        }

        if (category != null && category.length > 0) {
            List<String> categories = Arrays.asList(category);
            requestMessage.addAllCategory(categories);
        }

        if (priority != null && priority.length > 0) {
            List<String> priorities = Arrays.asList(priority);
            requestMessage.addAllCategory(priorities);
        }

        if (status != null && status.length > 0) {
            List<String> statuses = Arrays.asList(status);
            requestMessage.addAllCategory(statuses);
        }

        // open date
        if (!"".equals(openDateStart) && !"".equals(openDateEnd)) {
            requestMessage.setOpenDateStart(openDateStart);
            requestMessage.setOpenDateEnd(openDateEnd);
        } else if (!"".equals(openDateStart)) {
            requestMessage.setOpenDateStart(openDateStart);
            requestMessage.setOpenDateEnd(openDateStart);
        } else if (!"".equals(openDateEnd)) {
            requestMessage.setOpenDateStart(openDateEnd);
            requestMessage.setOpenDateEnd(openDateEnd);
        }

        //close date
        if (!"".equals(closeDateStart) && !"".equals(closeDateEnd)) {
            requestMessage.setCloseDateStart(closeDateStart);
            requestMessage.setCloseDateEnd(closeDateEnd);
        } else if (!"".equals(closeDateStart)) {
            requestMessage.setCloseDateStart(closeDateStart);
            requestMessage.setCloseDateEnd(closeDateStart);
        } else if (!"".equals(closeDateEnd)) {
            requestMessage.setCloseDateStart(closeDateEnd);
            requestMessage.setCloseDateEnd(closeDateEnd);
        }

        //deadline
        if (!"".equals(deadlineStart) && !"".equals(deadlineEnd)) {
            requestMessage.setDeadlineStart(deadlineStart);
            requestMessage.setDeadlineEnd(deadlineEnd);
        } else if (!"".equals(deadlineStart)) {
            requestMessage.setDeadlineStart(deadlineStart);
            requestMessage.setDeadlineEnd(deadlineStart);
        } else if (!"".equals(deadlineEnd)) {
            requestMessage.setDeadlineStart(deadlineEnd);
            requestMessage.setDeadlineEnd(deadlineEnd);
        }

        Request request = new Request.Builder()
                .url(backendURL + "search")
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
                    model.put("tickets", msg.getSearchOperation().getSearchOpResponse().getTicketInfoList());
                }
            } else {
                logger.warn("Error received from backend, unable to get search result: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
        }

        return "searchResult";
    }

    @RequestMapping(value = "searchUsers", method = RequestMethod.GET)
    @ResponseBody
    List<String> searchUsers(@RequestParam("term") String term) {
        Request usersRequest = new Request.Builder()
                .url(backendURL + "searchUsers/" + term)
                .build();

        try (Response response = httpClient.newCall(usersRequest).execute()) {
            if (response.code() == 200) {
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

    private Msg wrapIntoMsg(Msg.SearchOp.SearchOpRequest.Builder requestMessage) {
        return Msg.newBuilder()
                .setSearchOperation(
                        Msg.SearchOp.newBuilder()
                                .setSearchOpRequest(requestMessage)
                )
                .build();
    }

}
