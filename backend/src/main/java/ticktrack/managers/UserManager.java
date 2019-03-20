package ticktrack.managers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import ticktrack.entities.PasswordLink;
import ticktrack.entities.User;
import ticktrack.entities.UserGroup;
import ticktrack.enums.Gender;
import common.enums.UserRole;
import ticktrack.interfaces.IUserManager;
import ticktrack.proto.Msg;
import ticktrack.repositories.GroupRepository;
import ticktrack.repositories.PasswordLinkRepository;
import ticktrack.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import common.helpers.PasswordHandler;
import ticktrack.util.NotificationSender;

import static ticktrack.proto.Msg.*;
import static ticktrack.util.ResponseHandler.*;
import static ticktrack.util.ResponseHandler.buildFailureResponse;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service("userMng")
public class UserManager implements IUserManager {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final NotificationSender notificationSender;
    private final PasswordLinkRepository passwordLinkRepository;
    private Logger logger = LoggerFactory.getLogger(User.class);

    @Autowired
    public UserManager(UserRepository userRepository, GroupRepository groupRepository, NotificationSender notificationSender, PasswordLinkRepository passwordLinkRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.notificationSender = notificationSender;
        this.passwordLinkRepository = passwordLinkRepository;
    }

    @Transactional
    @Override
    public CommonResponse create(UserOp.UserOpCreateRequest request) {
        String responseText;
        CommonResponse response;
        UserRole userRole;
        if (request != null) {
            Optional<User> searchResult = userRepository.findByUsername(request.getUsername());

            if (searchResult.isPresent()) {
                response = buildFailureResponse("User with this username already exists");
            } else {
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
                    newUser.setRegistrationTime(new Timestamp(getCurrentTimeInMillis()));

                    userRepository.save(newUser);
                    responseText = "User " + newUser.getUsername() + " created!";
                    logger.debug(responseText);

                    response = buildSuccessResponse(responseText);
                } catch (IllegalArgumentException e) {
                    responseText = "UserRole doesn't match with existing types!";
                    logger.warn(responseText);
                    response = buildFailureResponse(responseText);
                }
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
    public Msg update(UserOp.UserOpUpdateRequest request) {
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
                //response = buildSuccessResponse(responseText.toString());
                return wrapIntoMsg(buildUserInfo(user));
            } else {
                logger.warn(responseText.toString());
                response = buildFailureResponse(responseText.toString());
            }

        } else {
            responseText.append("There is no user with username ").append(request.getUsername());
            logger.warn(responseText.toString());
            response = buildFailureResponse(responseText.toString());
        }
        return wrapCommonResponseIntoMsg(response);
    }

    @Transactional
    @Override
    public CommonResponse changePassword(UserOp.UserOpChangePassword request) {
        String responseText;
        CommonResponse response;
        Optional<User> result = userRepository.findById(request.getUsername());

        if (result.isPresent()) {
            User user = result.get();

            user.setPassword(request.getNewPassword());
            passwordLinkRepository.delete(user.getPasswordLink());
            user.setPasswordLink(null);
            userRepository.save(user);

            responseText = "User " + user.getUsername() + "'s password is updated!";
            logger.debug(responseText);
            return buildSuccessResponse(responseText);
        } else {
            responseText = "There is no user with username " + request.getUsername();
            logger.warn(responseText);
            response = buildFailureResponse(responseText);
        }
        return response;
    }

    @Transactional
    @Override
    public CommonResponse generateChangePasswordLink(String username) {
        String responseText;
        Optional<User> result = userRepository.findById(username);

        if (result.isPresent()) {
            User user = result.get();

            String link = UUID.randomUUID().toString().replace("-", "");
            PasswordLink passwordLink = new PasswordLink();
            passwordLink.setLink(link);
            passwordLink.setValidDate(new Timestamp(getPasswordValidDate()));
            passwordLink.setUser(user);

            boolean messageSent = notificationSender.sendMail(user.getEmail(),
                    "Use this link to change your password:\nhttp://localhost:9203/changePassword/" + link);

//            if(messageSent) {
                passwordLinkRepository.save(passwordLink);
                logger.debug("Change password link generated for user {}", username);
                return buildSuccessResponse("Change password link generated. Notification sent");
//            } else {
//                logger.warn("Message was not sent");
//                return buildFailureResponse("Unable to send message");
//            }
        } else {
            responseText = "There is no user with username " + username;
            logger.warn(responseText);
            return buildFailureResponse(responseText);
        }
    }

    @Transactional
    @Override
    public CommonResponse validatePasswordLink(UserOp.UserOpValidatePasswordLink request) {
        String responseText;
        Optional<User> result = userRepository.findById(request.getUsername());

        if (result.isPresent()) {
            User user = result.get();

            if (request.getLink().equals(user.getPasswordLink().getLink())) {
                return buildSuccessResponse("Password Change Link is valid");
            }

            responseText = "Invalid Password Change Link";
            logger.debug(responseText);
            return buildFailureResponse(responseText);
        } else {
            responseText = "There is no user with username " + request.getUsername();
            logger.warn(responseText);
            return buildFailureResponse(responseText);
        }
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
                user.setDeactivationTime(new Timestamp(getCurrentTimeInMillis()));
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

    @Transactional
    @Override
    public CommonResponse validateLoginInformation(LoginRequest request) {
        String responseText;
        Optional<User> result = userRepository.findByUsername(request.getUsername());

        if (result.isPresent()) {
            User user = result.get();
            if (user.isActive()) {
                if (PasswordHandler.verifyPassword(user.getPassword(), request.getPassword())) {
                    return buildSuccessResponse("Password is valid");
                }
                return buildFailureResponse("Password is invalid");
            }
            return buildFailureResponse("User is deactivated");
        }
        responseText = "User " + request.getUsername() + " not found";
        logger.debug(responseText);
        return buildFailureResponse(responseText);

    }

    private long getCurrentTimeInMillis() {
        return DateTime.now().withZone(DateTimeZone.forID("Asia/Yerevan")).getMillis();
    }

    private long getPasswordValidDate() {
        return DateTime.now().withZone(DateTimeZone.forID("Asia/Yerevan")).plusDays(1).getMillis();
    }

    private UserOp.UserOpGetResponse.UserInfo buildUserInfo(User user) {
        UserOp.UserOpGetResponse.UserInfo.Builder userInfo = UserOp.UserOpGetResponse.UserInfo.newBuilder()
                .setUsername(user.getUsername())
                .setFirstname(user.getFirstName())
                .setLastname(user.getLastName())
                .setGender(UserOp.Gender.valueOf(user.getGender().toString()))
                .setIsActive(user.isActive())
                .setEmail(user.getEmail())
                .setRegistrationTime(user.getRegistrationTime().toString())
                .setRole(Msg.UserRole.valueOf(user.getRole().toString()));

        if (user.getGroup() != null) {
            userInfo.setGroup(user.getGroup().getName());
        }

        if (user.getDeactivationTime() != null) {
            userInfo.setDeactivationTime(user.getDeactivationTime().toString());
        }

        return userInfo.build();
    }

    private Msg wrapIntoMsg(UserOp.UserOpGetResponse.UserInfo userInfo) {
        return Msg.newBuilder()
                .setUserOperation(Msg.UserOp.newBuilder()
                        .setUserOpGetResponse(Msg.UserOp.UserOpGetResponse.newBuilder()
                                .addUserInfo(userInfo)))
                .build();
    }
}
