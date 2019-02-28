package ticktrack.controllers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ticktrack.managers.SearchManager;
import ticktrack.proto.Msg;
import ticktrack.util.ResponseHandler;

@Controller
@RequestMapping(value = "backend/v1")
public class SearchController {
    private final SearchManager searchManager;
    private final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    public SearchController(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String searchTickets(@RequestBody String jsonRequest) {
        Msg.Builder builder = Msg.newBuilder();
        try {
            JsonFormat.parser().merge(jsonRequest, builder);
        } catch (InvalidProtocolBufferException e) {
            logger.error("Unable to parse request body to protobuf", e);
            return null;
        }

        Msg request = builder.build();

        if(request.hasSearchOperation() && request.getSearchOperation().hasSearchOpRequest()) {
            Msg.SearchOp.SearchOpResponse result = searchManager.searchByCriteria(request.getSearchOperation().getSearchOpRequest());

            try {
                return JsonFormat.printer().print(Msg.newBuilder()
                        .setSearchOperation(
                                Msg.SearchOp.newBuilder()
                                        .setSearchOpResponse(result)
                        )
                        .build());
            } catch (InvalidProtocolBufferException e) {
                logger.error("Unable to create response message", e);
                return null;
            }
        } else {
            try {
                return JsonFormat.printer().print(Msg.newBuilder()
                            .setCommonResponse(
                                    ResponseHandler.buildFailureResponse("No search request found")
                            )
                );
            } catch (InvalidProtocolBufferException e) {
                logger.error("Unable to create failure response message", e);
                return null;
            }
        }
    }
}
