package classes.interfaces;

import static ticktrack.proto.Msg.*;

public interface IUserManager {
   CommonResponse create(UserOp.UserOpCreateRequest request);

   CommonResponse update(UserOp.UserOpUpdateRequest request);

   CommonResponse changePassword(UserOp.UserOpChangePassword request);

   CommonResponse changeRole(UserOp.UserOpChangeRole request);

   CommonResponse deactivate(UserOp.UserOpDeactivateRequest request);

    UserOp.UserOpGetResponse get(String username);

    UserOp.UserOpGetResponse getByCriteria(UserOp.UserOpGetByCriteriaRequest request);
}
