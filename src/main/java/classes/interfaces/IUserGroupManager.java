package classes.interfaces;

import classes.entities.UserGroup;

import static ticktrack.proto.Msg.*;

public interface IUserGroupManager {
   CommonResponse createUserGroup(String groupName);

   CommonResponse deleteUserGroup(String groupName);

   CommonResponse changeName(UserGroupOp.UserGroupOpUpdateRequest request);

   UserGroupOp.UserGroupOpGetAllResponse getAll();
}
