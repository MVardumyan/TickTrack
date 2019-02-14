package classes.interfaces;

import classes.entities.User;
import ticktrack.proto.CommonResponse;
import ticktrack.proto.UserOp;

public interface IUserManager {
   CommonResponse create(UserOp.UserOpCreateRequest request);

   CommonResponse update(UserOp.UserOpUpdateRequest request);

   CommonResponse changePassword(UserOp.UserOpChangePassword request);

   CommonResponse changeRole(UserOp.UserOpChangeRole request);

   CommonResponse deactivate(UserOp.UserOpDeactivateRequest request);

    User get(String username);

    UserOp.UserOpGetResponse getByCriteria(UserOp.UserOpGetByCriteriaRequest request);
}
