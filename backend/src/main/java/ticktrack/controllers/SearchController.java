package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ticktrack.interfaces.ISearchManager;
import ticktrack.proto.Msg;

import java.util.List;

import static common.helpers.CustomJsonParser.*;
import static ticktrack.util.ResponseHandler.*;

@Controller
@RequestMapping(value = "backend/v1")
public class SearchController {
    private final ISearchManager searchManager;
    private final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    public SearchController(ISearchManager searchManager) {
        this.searchManager = searchManager;
    }

    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String searchTickets(@PathVariable("page") Integer page,
                         @PathVariable("size") Integer size,
                         @RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);
    ResponseEntity searchTickets(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {

            return buildFailedToParseResponse();

        } else if (request.hasSearchOperation() && request.getSearchOperation().hasSearchOpRequest()) {

            Msg.SearchOp.SearchOpResponse result = searchManager.searchByCriteria(request.getSearchOperation().getSearchOpRequest(),page,size);
            return ResponseEntity
                    .ok(protobufToJson(wrapIntoMsg(result)));
        }

        logger.warn("No search request found");
        return buildInvalidProtobufContentResponse("No search request found");

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
