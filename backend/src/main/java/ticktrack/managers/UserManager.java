package ticktrack.managers;

import ticktrack.entities.User;
import ticktrack.enums.UserRole;
import ticktrack.interfaces.IUserManager;
import ticktrack.proto.Msg;
import ticktrack.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ticktrack.proto.Msg.*;
import static ticktrack.util.ResponseHandler.*;

import javax.transaction.Transactional;
import java.util.Optional;

@Service("userMng")
public class UserManager implements IUserManager {
    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(User.class);

    @Autowired
    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                newUser.setActiveStatus(true);
                newUser.setEmail(request.getEmail());
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
        String responseText;
        CommonResponse response;
        Optional<User> result = userRepository.findById(request.getUsername());
        if (result.isPresent()) {
            User user = result.get();
            switch (request.getParamName()) {
                case FirstName:
                    user.setFirstName(request.getValue());
                    userRepository.save(user);
                    responseText = "User " + user.getUsername() + "'s First Name is updated!";
                    logger.debug(responseText);

                    response = buildSuccessResponse(responseText);
                    break;
                case LastName:
                    user.setLastName(request.getValue());
                    user.setFirstName(request.getValue());
                    userRepository.save(user);
                    responseText = "User " + user.getUsername() + "'s Last Name is updated!";
                    logger.debug(responseText);

                    response = buildSuccessResponse(responseText);
                    break;
                case Email:
                    user.setEmail(request.getValue());
                    user.setFirstName(request.getValue());
                    userRepository.save(user);
                    responseText = "User " + user.getUsername() + "'s Email is updated!";
                    logger.debug(responseText);

                    response = buildSuccessResponse(responseText);
                    break;
                default:
                    responseText = "Unexpected parameter name!";
                    logger.debug(responseText);

                    response = buildFailureResponse(responseText);
                    break;
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
    public CommonResponse changeRole(UserOp.UserOpChangeRole request) {
        String responseText;
        CommonResponse response;
        Optional<User> result = userRepository.findById(request.getUsername());
        if (result.isPresent()) {
            User user = result.get();
            UserRole userRole;
            try {
                userRole = UserRole.valueOf(request.getNewRole().toString());
                if (userRole.equals(user.getRole())) {
                    responseText = "Nothing changed. The user " + user.getUsername() + " had role " + user.getRole();
                    logger.debug(responseText);

                    response = buildFailureResponse(responseText);
                } else {
                    user.setRole(userRole);
                    userRepository.save(user);
                    responseText = "User " + user.getUsername() + "'s role updated to " + user.getRole();
                    logger.debug(responseText);

                    response = buildSuccessResponse(responseText);
                }
            } catch (IllegalArgumentException e) {
                responseText = "UserRole doesn't match with existing types!!";
                logger.debug(responseText);

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
    public CommonResponse deactivate(UserOp.UserOpDeactivateRequest request) {
        String responseText;
        CommonResponse response;
        Optional<User> result = userRepository.findById(request.getUsername());
        if (result.isPresent()) {
            User user = result.get();
            if (user.isActive()) {
                user.setActiveStatus(false);
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
            responseText = "There is no user with username " + request.getUsername();
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
            return null;
        }
    }

    @Transactional
    @Override
    public UserOp.UserOpGetResponse getByCriteria(UserOp.UserOpGetByCriteriaRequest request) {
        Iterable<User> result;
        UserOp.UserOpGetResponse.Builder responseBuilder = UserOp.UserOpGetResponse.newBuilder();

        if (request.getCriteria().equals(UserOp.UserOpGetByCriteriaRequest.Criteria.All)) {
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
                .setIsActive(user.isActive())
                .setEmail(user.getEmail())
                .setRole(Msg.UserRole.valueOf(user.getRole().toString()));

        if (user.getGroup() != null) {
            userInfo.setGroup(user.getGroup().getName());
        }

        return userInfo.build();
    }
}
