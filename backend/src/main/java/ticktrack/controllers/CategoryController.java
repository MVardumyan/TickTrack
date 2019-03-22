package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticktrack.interfaces.ICategoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ticktrack.proto.Msg;

import static common.helpers.CustomJsonParser.*;
import static ticktrack.util.ResponseHandler.*;

@Controller
@RequestMapping(path = "/backend/v1/categories")
public class CategoryController {
    private final ICategoryManager categoryManager;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public CategoryController(ICategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    @RequestMapping(path = "/add", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    ResponseEntity addCategory(@RequestParam("name") String categoryName) {
        Msg.CommonResponse result = categoryManager.createCategory(categoryName);
        return processManagerResponse(result);
    }

    @RequestMapping(path = "/deactivate", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    ResponseEntity deactivateCategory(@RequestParam("name") String categoryName) {
        Msg.CommonResponse result = categoryManager.deactivateCategory(categoryName);
        return processManagerResponse(result);
    }

    @RequestMapping(path = "/getAll", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String getAllCategories() {
        Msg.CategoryOp.CategoryOpGetAllResponse result = categoryManager.getAll();
        return protobufToJson(wrapIntoMsg(result));
    }

    @RequestMapping(path = "/changeName", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ResponseEntity changeCategoryName(@RequestBody String jsonRequest) {
        Msg request = jsonToProtobuf(jsonRequest);

        if (request == null) {
            return buildFailedToParseResponse();
        } else if (request.hasCategoryOperation()
                && request.getCategoryOperation().hasCategoryOpUpdateRequest()) {
            Msg.CommonResponse result = categoryManager.changeName(
                    request.getCategoryOperation().getCategoryOpUpdateRequest()
            );
            return processManagerResponse(result);
        }

        logger.warn("Request message should contain CategoryOp.CategoryOpUpdateRequest type");
        return buildInvalidProtobufContentResponse("Request message should contain CategoryOp.CategoryOpUpdateRequest type");
    }

    private Msg wrapIntoMsg(Msg.CategoryOp.CategoryOpGetAllResponse message) {
        return Msg.newBuilder()
                .setCategoryOperation(
                        Msg.CategoryOp.newBuilder()
                                .setCategoryOpGetAllResponse(message)
                ).build();
    }

}
