package classes.beans;

import classes.entities.User;
import classes.enums.UserRole;
import classes.interfaces.IUserManager;
import ticktrack.proto.CommonResponse;
import ticktrack.proto.UserOp;

public class UserManager implements IUserManager {

   @Override
   public CommonResponse create(UserOp.UserOpCreateRequest request) {
      return null;
   }

   @Override
   public CommonResponse update(UserOp.UserOpUpdateRequest request) {
      return null;
   }

   @Override
   public CommonResponse changePassword(UserOp.UserOpChangePassword request) {
      return null;
   }

   @Override
   public CommonResponse changeRole(UserOp.UserOpChangeRole request) {
      return null;
   }

   @Override
   public CommonResponse deactivate(UserOp.UserOpDeactivateRequest request) {
      return null;
   }

   @Override
   public User get(String username) {
      return null;
   }

   @Override
   public UserOp.UserOpGetResponse getByCriteria(UserOp.UserOpGetByCriteriaRequest request) {
      return null;
   }
}
