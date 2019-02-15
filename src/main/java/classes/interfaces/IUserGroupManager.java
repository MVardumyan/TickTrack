package classes.interfaces;

import classes.entities.UserGroup;
import classes.entities.User;
import ticktrack.proto.CommonResponse;
import ticktrack.proto.UserGroupOp;

import java.util.Set;

public interface IUserGroupManager {
   CommonResponse groupOperation(UserGroupOp.UserGroupOpRequest request);

   CommonResponse changeName(UserGroupOp.UserGroupOpUpdateRequest request);

   UserGroup get(String name);

   UserGroupOp.UserGroupOpGetAllResponse getAll();
}
