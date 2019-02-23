package classes.beans;

import classes.entities.User;
import classes.enums.UserRole;
import classes.interfaces.IUserManager;
import classes.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ticktrack.proto.Msg;
import ticktrack.proto.UserOp;

import static ticktrack.proto.Msg.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.Optional;

@Service("userMng")
public class UserManager implements IUserManager {
   @Autowired
   private UserRepository userRepository;
   @Autowired
   private final EntityManager entityManager;
   private Logger logger = LoggerFactory.getLogger(User.class);

   public UserManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   @Transactional
   @Override
   public CommonResponse create(UserOp.UserOpCreateRequest request) {
      String responseText;
      CommonResponse response;
      UserRole userRole;
      if(request != null){
         try {
            userRole = UserRole.valueOf(request.getRole().toString());
            User newUser = new User(request.getUsername(),request.getFirstname(),request.getLastname(),
               request.getPassword(),userRole);
            newUser.setActiveStatus(true);
            responseText = "User " + newUser.getUsername() + " created!";
            logger.debug(responseText);

            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Success)
               .build();
         } catch (IllegalArgumentException e) {
            responseText = "UserRole doesn't match with existing types!";
            logger.warn(responseText);
            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Failure)
               .build();
         }
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
      CommonResponse response;
      Optional<User> result = userRepository.findById(request.getUsername());
      if(result.isPresent()){
         User user = result.get();
         switch (request.getParamName()){
            case FirstName:
               user.setFirstName(request.getValue());
               responseText = "User " + user.getUsername() + "'s First Name is updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
               break;
            case LastName:
               user.setLastName(request.getValue());
               user.setFirstName(request.getValue());
               responseText = "User " + user.getUsername() + "'s Last Name is updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
               break;
            case Email:
               user.setEmail(request.getValue());
               user.setFirstName(request.getValue());
               responseText = "User " + user.getUsername() + "'s Email is updated!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
               break;
               default:
                  responseText = "Unexpected parameter name!";
                  logger.debug(responseText);

                  response = CommonResponse.newBuilder()
                     .setResponseText(responseText)
                     .setResponseType(CommonResponse.ResponseType.Failure)
                     .build();
                  break;
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
      if(result.isPresent()){
         User user = result.get();
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
      CommonResponse response;
      Optional<User> result = userRepository.findById(request.getUsername());
      if(result.isPresent()){
         User user = result.get();
         if(user.getRole().equals(UserRole.valueOf(request.getNewRole().toString()))){
            responseText = "Nothing changed. The user " + user.getUsername() + " had role " + user.getRole();
            logger.debug(responseText);

            response = CommonResponse.newBuilder()
               .setResponseText(responseText)
               .setResponseType(CommonResponse.ResponseType.Failure)
               .build();
         }else {
            UserRole userRole;
            try {
               userRole = UserRole.valueOf(request.getNewRole().toString());
               user.setRole(userRole);
               responseText = "User " + user.getUsername() + "'s role updated to " + user.getRole();
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
            } catch (IllegalArgumentException e) {
               responseText = "UserRole doesn't match with existing types!!";
               logger.debug(responseText);

               response = CommonResponse.newBuilder()
                  .setResponseText(responseText)
                  .setResponseType(CommonResponse.ResponseType.Success)
                  .build();
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
      CommonResponse response;
      Optional<User> result = userRepository.findById(request.getUsername());
      if(result.isPresent()){
         User user = result.get();
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
   public UserOp.UserOpGetResponse get(String username) {
      Optional<User> result = userRepository.findByUsername(username);
      UserOp.UserOpGetResponse response;
      UserRole userRole = UserRole.valueOf(result.get().getRole().toString());
      if (result.isPresent()) {
         logger.debug("Query for {} user received", username);
         //response = Msg.UserOp.UserOpGetResponse.UserInfo.newBuilder().build();
         return null; //response; //todo
      } else {
         logger.debug("User {} not found", username);
         return null;
      }
   }

   @Transactional
   @Override
   public UserOp.UserOpGetResponse getByCriteria(UserOp.UserOpGetByCriteriaRequest request) {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
      Root<User> root = criteriaQuery.from(User.class);

      Predicate criteria = builder.conjunction();
      Predicate currentPredicate;

      //todo
      return null;
   }
}
