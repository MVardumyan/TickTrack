package ticktrack.controllers;

import ticktrack.managers.CategoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
   public @ResponseBody
   String addCategory(String categoryName) {
      Msg.CommonResponse result = categoryManager.createCategory(categoryName);
      return buildDefaultResponseMessage(result).toString();
   }

   @RequestMapping(path = "/deactivate")
   public @ResponseBody
   Msg deactivateCategory(String categoryName) {
      Msg.CommonResponse result = categoryManager.deactivateCategory(categoryName);
      return buildDefaultResponseMessage(result);
   }

   @RequestMapping(path = "getAll", method = RequestMethod.GET)
   public @ResponseBody
   Msg getAllCategories() {
      Msg.CategoryOp.CategoryOpGetAllResponse result = categoryManager.getAll();
      return Msg.newBuilder()
         .setCategoryOperation(
            Msg.CategoryOp.newBuilder()
               .setCategoryOpGetAllResponse(result)
         ).build();
   }

   @RequestMapping(path = "changeName")
   public @ResponseBody
   Msg changeCategoryName(@RequestBody Msg request) {
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
         .setTime(System.currentTimeMillis())
         .setUsername("someone")
         .setCommonResponse(response)
         .build();
   }

}
