package classes.beans;

import classes.entities.User;
import classes.enums.UserRole;
import classes.interfaces.IUserManager;
import classes.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static ticktrack.proto.Msg.*;

import javax.transaction.Transactional;
import java.util.Optional;

public class UserManager implements IUserManager {
   @Autowired
   private UserRepository userRepository;
   private Logger logger = LoggerFactory.getLogger(User.class);

   @Transactional
   @Override
   public CommonResponse create(UserOp.UserOpCreateRequest request) {
      String responseText;
      CommonResponse response;
      if(request != null){
         UserRole userRole = null; // todo i think i did this in bad way
         for (UserRole role: UserRole.values()) {
            if(role.equals(request.getRole())){
               userRole = role;
               break;
            }else{
               responseText = "Requests UserRole doesn't match with existing types!"; //todo check this part
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Failure)
                  .build();
            }
         }

         User newUser = new User(request.getUsername(),request.getFirstname(),request.getLastname(),
         request.getPassword(),userRole);

         responseText = "User " + newUser.getUsername() + " created!";
         logger.debug(responseText);

         response = CommonResponse.newBuilder()
            .setResponseText(responseText)
            .setResponseType(CommonResponse.ResponseType.Success)
            .build();
      }else {
         responseText = "Request to create a User is null";
         logger.warn(responseText);
         response = CommonResponse.newBuilder()
            .setResponseText(responseText)
            .setResponseType(CommonResponse.ResponseType.Failure)
            .build();
      }
      return response;
   }

   @Transactional
   @Override
   public CommonResponse update(UserOp.UserOpUpdateRequest request) {
      String responseText;
      CommonResponse response = null; //todo
      Optional<User> result = userRepository.findById(request.getUsername());
      User user = result.get();
      if(result.isPresent()){
         switch (request.getParamName()){
            case FirstName:
               user.setFirstName(request.getValue());
               responseText = "User " + user.getUsername() + "'s First Name is updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
            case LastName:
               user.setLastName(request.getValue());
               user.setFirstName(request.getValue());
               responseText = "User " + user.getUsername() + "'s Last Name is updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
            case Email:
               user.setEmail(request.getValue());
               user.setFirstName(request.getValue());
               responseText = "User " + user.getUsername() + "'s Email is updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
         }
      }else {
         responseText = "There is no user with username " + request.getUsername();
         logger.warn(responseText);
         response = CommonResponse.newBuilder()
            .setResponseText(responseText)
            .setResponseType(CommonResponse.ResponseType.Failure)
            .build();
      }
      return response;
   }

   @Transactional
   @Override
   public CommonResponse changePassword(UserOp.UserOpChangePassword request) {
      String responseText;
      CommonResponse response;
      Optional<User> result = userRepository.findById(request.getUsername());
      User user = result.get();
      if(result.isPresent()){
         if(user.getPassword().equals(request.getOldPassword())){
            user.setPassword(request.getNewPassword());
            responseText = "User " + user.getUsername() + "'s password is updated!";
            logger.debug(responseText);

            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Success)
               .build();
         }else{
            responseText = "User " + request.getUsername() + "'s old password doesn't match!";
            logger.warn(responseText);
            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Failure)
               .build();
         }
      }else{
         responseText = "There is no user with username " + request.getUsername();
         logger.warn(responseText);
         response = CommonResponse.newBuilder()
            .setResponseText(responseText)
            .setResponseType(CommonResponse.ResponseType.Failure)
            .build();
      }
      return response;
   }

   @Transactional
   @Override
   public CommonResponse changeRole(UserOp.UserOpChangeRole request) {
      String responseText;
      CommonResponse response = null; //todo
      Optional<User> result = userRepository.findById(request.getUsername());
      User user = result.get();
      if(result.isPresent()){
         if(user.getRole().equals(request.getNewRole())){
            responseText = "User " + user.getUsername() + "'s role was " + user.getRole();
            logger.debug(responseText);

            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Success)
               .build();
         }else {
            for (UserRole userRole: UserRole.values()) {
               if(userRole.equals(user.getRole())){
                  user.setRole(userRole);
                  responseText = "User " + user.getUsername() + "'s role updated to " + user.getRole();
                  logger.debug(responseText);

                  response = CommonResponse.newBuilder()
                     .setResponseText(responseText)
                     .setResponseType(CommonResponse.ResponseType.Success)
                     .build();
               }else {
                  responseText = "Role doesn't match with the type!";
                  logger.debug(responseText);

                  response = CommonResponse.newBuilder()
                     .setResponseText(responseText)
                     .setResponseType(CommonResponse.ResponseType.Success)
                     .build();
               }
            }
         }
      }else{
         responseText = "There is no user with username " + request.getUsername();
         logger.warn(responseText);
         response = CommonResponse.newBuilder()
            .setResponseText(responseText)
            .setResponseType(CommonResponse.ResponseType.Failure)
            .build();
      }
      return response;
   }

   @Transactional
   @Override
   public CommonResponse deactivate(UserOp.UserOpDeactivateRequest request) {
      String responseText;
      CommonResponse response = null; //todo
      Optional<User> result = userRepository.findById(request.getUsername());
      User user = result.get();
      if(result.isPresent()){
         if (user.isActive()){
            user.setActiveStatus(false);
            responseText = "User " + user.getUsername() + " is deactivated.";
            logger.warn(responseText);
            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Failure)
               .build();
         }else{
            responseText = "User is already not active.";
            logger.warn(responseText);
            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Failure)
               .build();
         }
      }else{
         responseText = "There is no user with username " + request.getUsername();
         logger.warn(responseText);
         response = CommonResponse.newBuilder()
            .setResponseText(responseText)
            .setResponseType(CommonResponse.ResponseType.Failure)
            .build();
      }
      return response;
   }

   @Transactional
   @Override
   public User get(String username) {
      Optional<User> result = userRepository.findByName(username);
      if (result.isPresent()) {
         logger.debug("Query for {} user received", username);
         return result.get();
      } else {
         logger.debug("User {} not found", username);
         return null;
      }
   }

   @Transactional
   @Override
   public UserOp.UserOpGetResponse getByCriteria(UserOp.UserOpGetByCriteriaRequest request) {
      return null; //todo need to be discussed
   }
}
