package ticktrack.controllers;

import org.springframework.web.bind.annotation.*;
import ticktrack.managers.CategoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ticktrack.proto.Msg;

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
   public Msg addCategory(@RequestParam("name") String categoryName) {
      Msg.CommonResponse result = categoryManager.createCategory(categoryName);
      return buildDefaultResponseMessage(result);
   }

   @RequestMapping(path = "/deactivate")
   @ResponseBody
   public Msg deactivateCategory(@RequestParam("name") String categoryName) {
      Msg.CommonResponse result = categoryManager.deactivateCategory(categoryName);
      return buildDefaultResponseMessage(result);
   }

   @RequestMapping(path = "/getAll", method = RequestMethod.GET)
   @ResponseBody
   public Msg getAllCategories() {
      Msg.CategoryOp.CategoryOpGetAllResponse result = categoryManager.getAll();
      return Msg.newBuilder()
         .setCategoryOperation(
            Msg.CategoryOp.newBuilder()
               .setCategoryOpGetAllResponse(result)
         ).build();
   }

   @RequestMapping(path = "/changeName")
   @ResponseBody
   public Msg changeCategoryName(@RequestBody Msg request) {
      Msg.CommonResponse result;

      if (request.hasCategoryOperation()
         && request.getCategoryOperation().hasCategoryOpUpdateRequest()) {
         result = categoryManager.changeName(
            request.getCategoryOperation().getCategoryOpUpdateRequest()
         );
      } else {
         result = Msg.CommonResponse.newBuilder()
            .setResponseText("Request message should contain CategoryOp.CategoryOpUpdateRequest type")
            .setResponseType(Msg.CommonResponse.ResponseType.Failure)
            .build();
      }

      return buildDefaultResponseMessage(result);
   }

   private Msg buildDefaultResponseMessage(Msg.CommonResponse response) {
      return Msg.newBuilder()
         .setCommonResponse(response)
         .build();
   }

}
