package ticktrack.managers;

import ticktrack.entities.User;
import ticktrack.entities.UserGroup;
import ticktrack.enums.Gender;
import ticktrack.enums.UserRole;
import ticktrack.interfaces.IUserManager;
import ticktrack.proto.Msg;
import ticktrack.repositories.GroupRepository;
import ticktrack.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ticktrack.proto.Msg.*;
import static ticktrack.util.ResponseHandler.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;

@Service("userMng")
public class UserManager implements IUserManager {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private Logger logger = LoggerFactory.getLogger(User.class);

    @Autowired
    public UserManager(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    @Override
    public CommonResponse create(UserOp.UserOpCreateRequest request) {
        String responseText;
        CommonResponse response;
        UserRole userRole;
        if (request != null) {
            try {
                userRole = UserRole.valueOf(request.getRole().toString());
                User newUser = new User(request.getUsername(), request.getFirstname(), request.getLastname(),
                        request.getPassword(), userRole);

                if (request.hasGroup()) {
                    Optional<UserGroup> groupResult = groupRepository.findByName(request.getGroup());
                    if (groupResult.isPresent()) {
                        newUser.setGroup(groupResult.get());
                    } else {
                        responseText = "Group {} doesn't match with existing types!";
                        logger.warn(responseText);
                        return buildFailureResponse(responseText);
                    }
                }

                newUser.setActiveStatus(true);
                newUser.setEmail(request.getEmail());
                newUser.setGender(Gender.valueOf(request.getGender().toString()));
                newUser.setRegistrationTime(new Timestamp(System.currentTimeMillis()));

                userRepository.save(newUser);
                responseText = "User " + newUser.getUsername() + " created!";
                logger.debug(responseText);

                response = buildSuccessResponse(responseText);
            } catch (IllegalArgumentException e) {
                responseText = "UserRole doesn't match with existing types!";
                logger.warn(responseText);
                response = buildFailureResponse(responseText);
            }
        } else {
            responseText = "Request to create a User is null";
            logger.warn(responseText);
            response = buildFailureResponse(responseText);
        }
        return response;
    }

    @Transactional
    @Override
    public CommonResponse update(UserOp.UserOpUpdateRequest request) {
        StringBuilder responseText = new StringBuilder();
        CommonResponse response;
        Optional<User> result = userRepository.findById(request.getUsername());

        if (result.isPresent()) {
            User user = result.get();
            boolean updateSuccess = false;

            if (request.hasFirstName()) {
                user.setFirstName(request.getFirstName());

                responseText.append("User ").append(user.getUsername()).append("'s First Name is updated!\n");
                updateSuccess = true;
            }
            if (request.hasLastName()) {
                user.setLastName(request.getLastName());

                responseText.append("User ").append(user.getUsername()).append("'s Last Name is updated!\n");
                updateSuccess = true;
            }
            if (request.hasEmail()) {
                user.setEmail(request.getEmail());

                responseText.append("User ").append(user.getUsername()).append("'s Email is updated!\n");
                updateSuccess = true;
            }
            if (request.hasGender()) {
                try {
                    Gender gender = Gender.valueOf(request.getGender().toString());

                    user.setGender(gender);
                    updateSuccess = true;
                } catch (IllegalArgumentException e) {
                    responseText.append("Unknown Gender type");
                }
            }
            if (request.hasGroup()) {
                Optional<UserGroup> groupResult = groupRepository.findByName(request.getGroup());
                if (groupResult.isPresent()) {
                    UserGroup group = groupResult.get();

                    if (group.equals(user.getGroup())) {
                        responseText.append("Nothing changed. The user ").append(user.getUsername()).append(" is in group ").append(user.getGroup());
                    } else {
                        user.setGroup(group);
                        responseText.append("User ").append(request.getUsername()).append("'s group updated!\n");
                        updateSuccess = true;
                    }
                } else {
                    responseText.append("Group ").append(request.getGroup()).append(" not found");
                }
            }
            if (request.hasRole()) {
                try {
                    UserRole userRole = UserRole.valueOf(request.getRole().toString());
                    if (userRole.equals(user.getRole())) {
                        responseText.append("Nothing changed. The user ").append(user.getUsername()).append(" had role ").append(user.getRole());
                    } else {
                        user.setRole(userRole);
                        responseText.append("User ").append(user.getUsername()).append("'s role updated to ").append(user.getRole());
                        updateSuccess = true;
                    }
                } catch (IllegalArgumentException e) {
                    responseText.append("UserRole doesn't match with existing types!");
                }
            }

            if (updateSuccess) {
                userRepository.save(user);
                logger.debug(responseText.toString());
                response = buildSuccessResponse(responseText.toString());
            } else {
                logger.warn(responseText.toString());
                response = buildFailureResponse(responseText.toString());
            }

        } else {
            responseText.append("There is no user with username ").append(request.getUsername());
            logger.warn(responseText.toString());
            response = buildFailureResponse(responseText.toString());
        }
        return response;
    }

    @Transactional
    @Override
    public CommonResponse changePassword(UserOp.UserOpChangePassword request) {
        String responseText;
        CommonResponse response;
        Optional<User> result = userRepository.findById(request.getUsername());
        if (result.isPresent()) {
            User user = result.get();
            if (user.getPassword().equals(request.getOldPassword())) {
                user.setPassword(request.getNewPassword());
                userRepository.save(user);
                responseText = "User " + user.getUsername() + "'s password is updated!";
                logger.debug(responseText);

                response = buildSuccessResponse(responseText);
            } else {
                responseText = "User " + request.getUsername() + "'s old password doesn't match!";
                logger.warn(responseText);
                response = buildFailureResponse(responseText);
            }
        } else {
            responseText = "There is no user with username " + request.getUsername();
            logger.warn(responseText);
            response = buildFailureResponse(responseText);
        }
        return response;
    }

    @Transactional
    @Override
    public CommonResponse deactivate(String username) {
        String responseText;
        CommonResponse response;
        Optional<User> result = userRepository.findById(username);
        if (result.isPresent()) {
            User user = result.get();
            if (user.isActive()) {
                user.setActiveStatus(false);
                user.setDeactivationTime(new Timestamp(System.currentTimeMillis()));
                userRepository.save(user);
                responseText = "User " + user.getUsername() + " is deactivated.";
                logger.warn(responseText);
                response = buildSuccessResponse(responseText);
            } else {
                responseText = "User is already not active.";
                logger.warn(responseText);
                response = buildFailureResponse(responseText);
            }
        } else {
            responseText = "There is no user with username " + username;
            logger.warn(responseText);
            response = buildFailureResponse(responseText);
        }
        return response;
    }

    @Transactional
    @Override
    public UserOp.UserOpGetResponse get(String username) {
        Optional<User> result = userRepository.findByUsername(username);

        if (result.isPresent()) {
            User user = result.get();
            logger.debug("Query for {} user received", username);

            return UserOp.UserOpGetResponse.newBuilder()
                    .addUserInfo(buildUserInfo(user))
                    .build();
        } else {
            logger.debug("User {} not found", username);
            return UserOp.UserOpGetResponse.newBuilder().build();
        }
    }

    @Transactional
    @Override
    public UserOp.UserOpGetResponse getByRole(UserOp.UserOpGetByRoleRequest request) {
        Iterable<User> result;
        UserOp.UserOpGetResponse.Builder responseBuilder = UserOp.UserOpGetResponse.newBuilder();

        if (request.getCriteria().equals(UserOp.UserOpGetByRoleRequest.Criteria.All)) {
            result = userRepository.findAll();
        } else {
            try {
                UserRole role = UserRole.valueOf(request.getCriteria().toString());
                result = userRepository.findAllByRole(role);
            } catch (IllegalArgumentException e) {
                logger.warn("Role {} does not exist", request.getCriteria());
                return null;
            }
        }

        for (User user : result) {
            responseBuilder.addUserInfo(buildUserInfo(user));
        }

        return responseBuilder.build();
    }

    private UserOp.UserOpGetResponse.UserInfo buildUserInfo(User user) {
        UserOp.UserOpGetResponse.UserInfo.Builder userInfo = UserOp.UserOpGetResponse.UserInfo.newBuilder()
                .setUsername(user.getUsername())
                .setFirstname(user.getFirstName())
                .setLastname(user.getLastName())
                .setGender(UserOp.Gender.valueOf(user.getGender().toString()))
                .setIsActive(user.isActive())
                .setEmail(user.getEmail())
                .setRegistrationTime(user.getRegistrationTime().getTime())
                .setRole(Msg.UserRole.valueOf(user.getRole().toString()));

        if (user.getGroup() != null) {
            userInfo.setGroup(user.getGroup().getName());
        }

        if (user.getDeactivationTime() != null) {
            userInfo.setDeactivationTime(user.getDeactivationTime().getTime());
        }

        return userInfo.build();
    }
}
