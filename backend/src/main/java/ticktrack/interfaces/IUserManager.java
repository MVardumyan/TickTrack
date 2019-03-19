package ticktrack.interfaces;

import ticktrack.proto.Msg;

import static ticktrack.proto.Msg.*;

public interface IUserManager {
    CommonResponse create(UserOp.UserOpCreateRequest request);

    Msg update(UserOp.UserOpUpdateRequest request);

    CommonResponse changePassword(UserOp.UserOpChangePassword request);

    CommonResponse generateChangePasswordLink(String username);

    CommonResponse validatePasswordLink(UserOp.UserOpValidatePasswordLink request);

    CommonResponse deactivate(String username);

    UserOp.UserOpGetResponse get(String username);

    UserOp.UserOpGetResponse getByRole(UserOp.UserOpGetByRoleRequest request);

    CommonResponse validateLoginInformation(LoginRequest request);
}
