package classes.beans;

import classes.entities.User;
import classes.enums.UserRole;
import classes.interfaces.IUserManager;
import classes.repositories.UserRepository;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ticktrack.proto.CommonResponse;
import ticktrack.proto.UserOp;

import javax.management.relation.Role;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserManager implements IUserManager {
   @Autowired
   private UserRepository userRepository;
   private Logger logger = LoggerFactory.getLogger(User.class);

   @Override
   public CommonResponse create(UserOp.UserOpCreateRequest request) {
      return null;
   }

   @Override
   public CommonResponse update(UserOp.UserOpUpdateRequest request) {
      return null;
   }

   @Override
   public CommonResponse changePassword(UserOp.UserOpChangePassword request) {
      return null;
   }

   @Override
   public CommonResponse changeRole(UserOp.UserOpChangeRole request) {
      return null;
   }

   @Override
   public CommonResponse deactivate(UserOp.UserOpDeactivateRequest request) {
      return null;
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

   @Override
   public UserOp.UserOpGetResponse getByCriteria(UserOp.UserOpGetByCriteriaRequest request) {
      return null; //todo
   }
}
