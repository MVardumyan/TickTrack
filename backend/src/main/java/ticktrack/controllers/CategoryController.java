package ticktrack.controllers;

import org.springframework.web.bind.annotation.*;
import ticktrack.managers.CategoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ticktrack.proto.Msg;
import static ticktrack.util.CustomJsonParser.*;
import static ticktrack.util.ResponseHandler.*;

@Controller
@RequestMapping(path = "/backend/v1/categories")
public class CategoryController {
   private final CategoryManager categoryManager;

   @Autowired
   public CategoryController(CategoryManager categoryManager) {
      this.categoryManager = categoryManager;
   }

   @RequestMapping(path = "/add", method = RequestMethod.POST)
   @ResponseBody
   public String addCategory(@RequestParam("name") String categoryName) {
      Msg.CommonResponse result = categoryManager.createCategory(categoryName);
      return protobufToJson(wrapIntoMsg(result));
   }

   @RequestMapping(path = "/deactivate")
   @ResponseBody
   public String deactivateCategory(@RequestParam("name") String categoryName) {
      Msg.CommonResponse result = categoryManager.deactivateCategory(categoryName);
      return protobufToJson(wrapIntoMsg(result));
   }

   @RequestMapping(path = "/getAll", method = RequestMethod.GET)
   @ResponseBody
   public String getAllCategories() {
      Msg.CategoryOp.CategoryOpGetAllResponse result = categoryManager.getAll();
      return protobufToJson(wrapIntoMsg(result));
   }

   @RequestMapping(path = "/getAllActive", method = RequestMethod.GET)
   @ResponseBody
   public String getAllActiveCategories() {
      Msg.CategoryOp.CategoryOpGetAllResponse result = categoryManager.getAllActiveCategories();
      return protobufToJson(wrapIntoMsg(result));
   }

   @RequestMapping(path = "/changeName")
   @ResponseBody
   public String changeCategoryName(@RequestBody String jsonRequest) {
      Msg request = jsonToProtobuf(jsonRequest);

      Msg.CommonResponse result;

      if(request == null) {
         return protobufToJson(wrapIntoMsg(buildFailureResponse("Internal Error: unable to parse request to protobuf")));
      } else if (request.hasCategoryOperation()
         && request.getCategoryOperation().hasCategoryOpUpdateRequest()) {
         result = categoryManager.changeName(
            request.getCategoryOperation().getCategoryOpUpdateRequest()
         );
      } else {
         result = buildFailureResponse("Request message should contain CategoryOp.CategoryOpUpdateRequest type");
      }

      return protobufToJson(wrapIntoMsg(result));
   }

   private Msg wrapIntoMsg(Msg.CommonResponse message) {
      return Msg.newBuilder()
              .setCommonResponse(message)
              .build();
   }

   private Msg wrapIntoMsg(Msg.CategoryOp.CategoryOpGetAllResponse message) {
      return Msg.newBuilder()
              .setCategoryOperation(
                      Msg.CategoryOp.newBuilder()
                              .setCategoryOpGetAllResponse(message)
              ).build();
   }

}
