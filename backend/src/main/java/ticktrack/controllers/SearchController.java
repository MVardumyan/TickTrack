package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.managers.SearchManager;
import ticktrack.proto.Msg;

import java.util.List;

import static ticktrack.util.CustomJsonParser.*;
import static ticktrack.util.ResponseHandler.*;

@Controller
@RequestMapping(value = "backend/v1")
public class SearchController {
    private final SearchManager searchManager;
    private final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    public SearchController(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String searchTickets(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            if(request == null) {

                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));

            } else if (request.hasSearchOperation() && request.getSearchOperation().hasSearchOpRequest()) {

                Msg.SearchOp.SearchOpResponse result = searchManager.searchByCriteria(request.getSearchOperation().getSearchOpRequest());
                return protobufToJson(wrapIntoMsg(result));

            }

            logger.warn("No search request found");
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("No search request found")));
        } catch (Throwable t) {
            logger.error("Exception appear while handling search request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    @RequestMapping(value = "/searchUsers/{term}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<String> searchUsers(@PathVariable("term") String term) {
        return searchManager.searchUsersByTerm(term);
    }

    private Msg wrapIntoMsg(Msg.SearchOp.SearchOpResponse message) {
        return Msg.newBuilder()
                .setSearchOperation(
                        Msg.SearchOp.newBuilder()
                                .setSearchOpResponse(message)
                )
                .build();
    }
}
