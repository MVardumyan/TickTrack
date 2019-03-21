package ticktrack.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ticktrack.interfaces.ICategoryManager;
import ticktrack.managers.CategoryManager;
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
    String addCategory(@RequestParam("name") String categoryName) {
        Msg.CommonResponse result = categoryManager.createCategory(categoryName);
        return protobufToJson(wrapCommonResponseIntoMsg(result));
    }

    @RequestMapping(path = "/deactivate", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String deactivateCategory(@RequestParam("name") String categoryName) {
        Msg.CommonResponse result = categoryManager.deactivateCategory(categoryName);
        return protobufToJson(wrapCommonResponseIntoMsg(result));
    }

    @RequestMapping(path = "/getAll", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String getAllCategories() {
        Msg.CategoryOp.CategoryOpGetAllResponse result = categoryManager.getAll();
        return protobufToJson(wrapIntoMsg(result));
    }

    @RequestMapping(path = "/changeName", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String changeCategoryName(@RequestBody String jsonRequest) {
        try {
            Msg request = jsonToProtobuf(jsonRequest);

            Msg.CommonResponse result;

            if (request == null) {
                return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
            } else if (request.hasCategoryOperation()
                    && request.getCategoryOperation().hasCategoryOpUpdateRequest()) {
                result = categoryManager.changeName(
                        request.getCategoryOperation().getCategoryOpUpdateRequest()
                );
            } else {
                result = buildFailureResponse("Request message should contain CategoryOp.CategoryOpUpdateRequest type");
            }

            return protobufToJson(wrapCommonResponseIntoMsg(result));
        } catch (Throwable t) {
            logger.error("Exception appear while handling search request", t);
            return protobufToJson(wrapCommonResponseIntoMsg(buildFailureResponse("Internal Error\n" + t.getMessage())));
        }
    }

    private Msg wrapIntoMsg(Msg.CategoryOp.CategoryOpGetAllResponse message) {
        return Msg.newBuilder()
                .setCategoryOperation(
                        Msg.CategoryOp.newBuilder()
                                .setCategoryOpGetAllResponse(message)
                ).build();
    }

}
