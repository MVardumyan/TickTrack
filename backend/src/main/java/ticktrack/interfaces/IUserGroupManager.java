package ticktrack.interfaces;

import ticktrack.entities.UserGroup;

import static ticktrack.proto.Msg.*;

public interface IUserGroupManager {
   CommonResponse createUserGroup(String groupName);

   CommonResponse deactivateUserGroup(String groupName);

   CommonResponse changeName(UserGroupOp.UserGroupOpUpdateRequest request);

   UserGroupOp.UserGroupOpGetAllResponse getAll();
}
