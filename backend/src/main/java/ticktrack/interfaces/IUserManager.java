package ticktrack.interfaces;

import static ticktrack.proto.Msg.*;

public interface IUserManager {
   CommonResponse create(UserOp.UserOpCreateRequest request);

   CommonResponse update(UserOp.UserOpUpdateRequest request);

   CommonResponse changePassword(UserOp.UserOpChangePassword request);

   CommonResponse deactivate(String username);

    UserOp.UserOpGetResponse get(String username);

    UserOp.UserOpGetResponse getByRole(UserOp.UserOpGetByRoleRequest request);

    CommonResponse validateLoginInformation(LoginRequest request);
}
