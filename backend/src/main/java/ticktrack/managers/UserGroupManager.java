package ticktrack.managers;

import ticktrack.entities.UserGroup;
import ticktrack.interfaces.IUserGroupManager;
import ticktrack.proto.Msg;
import ticktrack.repositories.GroupRepository;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticktrack.util.ResponseHandler;

import static ticktrack.proto.Msg.*;
import static ticktrack.util.ResponseHandler.*;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class provides methods for managing Category entity.
 * CategoryManager is Spring component. For db interaction it uses autowired crudRepository interfaces.
 * Contains business logic for new Category creation, changing name and deactivation.
 */
@Service("GroupMng")
public class UserGroupManager implements IUserGroupManager {
    private final GroupRepository groupRepository;
    private Logger logger = LoggerFactory.getLogger(UserGroupManager.class);

    @Autowired
    public UserGroupManager(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * Method for new group creation.
     * @param groupName new group name
     * @return protobuf type CommonResponse with responseType: 1) success if group created; 2) failure if group exists or given name is null
     */
    @Transactional
    @Override
    public CommonResponse createUserGroup(String groupName) {
        String responseText;
        if (groupName == null) {
            responseText = "Group name is null";
        } else {
            if (groupRepository.existsByName(groupName)) {
                responseText = "Group" + groupName + " already exists";
                logger.warn(responseText);
            } else {
                UserGroup group = new UserGroup();
                group.setName(groupName);
                groupRepository.save(group);
                responseText = "Group " + groupName + " created";
                logger.debug(responseText);

                return buildSuccessResponse(responseText);
            }
        }

        return buildFailureResponse(responseText);
    }

    /**
     * Method for deleting UserGroup from db.
     * @param groupName corresponding group name
     * @return protobuf type CommonResponse with responseType: 1) success if group deleted; 2) failure if group not found/contains users/given name is null
     */
    @Transactional
    @Override
    public CommonResponse deleteUserGroup(String groupName) {
        String responseText;
        if (groupName == null) {
            responseText = "Group name is null";
        } else {
            UserGroup group = get(groupName);

            if (group == null) {
                responseText = "Group " + groupName + " not found";
                logger.warn(responseText);
            } else {
                if (group.getMembers().size() == 0) {
                    groupRepository.delete(group);

                    responseText = "Group" + groupName + " deleted";
                    logger.debug(responseText);

                    return buildSuccessResponse(responseText);
                } else {
                    responseText = "Group" + groupName + " cannot be deleted : group contains users";
                    logger.warn(responseText);
                }
            }
        }

        return buildFailureResponse(responseText);
    }

    /**
     * Method for changing user group name
     * @param request protobuf type UserGroupOpUpdateRequest contains old and new names
     * @return CommonResponse with responseType: 1) success if name updated; 2) failure if old name does not match
     */
    @Transactional
    @Override
    public CommonResponse changeName(UserGroupOp.UserGroupOpUpdateRequest request) {
        String responseText;

        if (request == null) {
            responseText = "Request is null";
            logger.warn(responseText);
        } else {
            UserGroup group = get(request.getOldName());

            if (group == null) {
                responseText = "Group " + request.getOldName() + " not found";
            } else {
                group.setName(request.getNewName());
                groupRepository.save(group);

                responseText = "Group name " + request.getOldName() + " updated to " + request.getNewName();
                logger.debug(responseText);
                return buildSuccessResponse(responseText);
            }
        }

        return buildFailureResponse(responseText);
    }

    /**
     * Method used inside UserGroupManager for searching group by name
     * @param name group name
     * @return 1) UserGroup entity; 2) null if not found
     */
    @Transactional
    public UserGroup get(String name) {
        Optional<UserGroup> result = groupRepository.findByName(name);
        if (result.isPresent()) {
            logger.debug("Query for {} group received", name);
            return result.get();
        } else {
            logger.debug("Group {} not found", name);
            return null;
        }
    }

    /**
     * Method for getting all groups from db.
     * @return protobuf type UserGroupOpGetAllResponse containing list of UserGroup String names.
     */
    @Transactional
    @Override
    public UserGroupOp.UserGroupOpGetAllResponse getAll() {
        return UserGroupOp.UserGroupOpGetAllResponse.newBuilder()
                .addAllGroupName(
                        Streams.stream(groupRepository.findAll()).map(UserGroup::getName).collect(Collectors.toList())
                )
                .build();
    }
}
