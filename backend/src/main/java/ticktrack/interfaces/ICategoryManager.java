package ticktrack.interfaces;

import ticktrack.entities.Category;

import static ticktrack.proto.Msg.*;

public interface ICategoryManager {
   CommonResponse createCategory(String categoryName);

   CommonResponse deactivateCategory(String categoryName);

   CommonResponse changeName(CategoryOp.CategoryOpUpdateRequest request);

   CategoryOp.CategoryOpGetAllResponse getAll();
}
