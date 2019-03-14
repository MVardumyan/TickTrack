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

@Service("GroupMng")
public class UserGroupManager implements IUserGroupManager {
    private final GroupRepository groupRepository;
    private Logger logger = LoggerFactory.getLogger(UserGroupManager.class);

    @Autowired
    public UserGroupManager(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

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

    @Transactional
    @Override
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
