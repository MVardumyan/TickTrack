package classes.interfaces;

import classes.entities.Category;
import ticktrack.proto.CategoryOp;
import ticktrack.proto.CommonResponse;

public interface ICategoryManager {
   CommonResponse categoryOperation(CategoryOp.CategoryOpRequest request);

   CommonResponse changeName(CategoryOp.CategoryOpUpdateRequest request);

   Category get(String name);

   CategoryOp.CategoryOpGetAllResponse getAll();
}
