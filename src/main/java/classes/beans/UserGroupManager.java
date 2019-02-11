package classes.beans;

import classes.entities.UserGroup;
import classes.interfaces.IUserGroupManager;
import classes.repositories.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserGroupManager implements IUserGroupManager {
   @Autowired
   private GroupRepository groupRepository;
   private Logger logger = LoggerFactory.getLogger(UserGroupManager.class);

   @Override
   @Transactional
   public boolean create(String name) {
      if(groupRepository.existsByName(name)) {
         logger.warn("Group {} already exists", name);
         return false;
      } else {
         UserGroup group = new UserGroup();
         group.setName(name);
         groupRepository.save(group);
         logger.debug("New group {} created and saved to db", name);
         return true;
      }
   }

   @Override
   @Transactional
   public boolean changeName(String oldName, String newName) {
      UserGroup group = get(oldName);

      if(group!=null) {
         group.setName(newName);
         groupRepository.save(group);
         logger.debug("Group name {} updated to {}", oldName, newName);
         return true;
      } else {
         return false;
      }
   }

   @Override
   @Transactional
   public boolean delete(String name) {
      UserGroup group = get(name);

      if(group!=null) {
         if (group.getMembers().size() == 0) {
            groupRepository.delete(group);
            logger.debug("Group {} deleted", name);
            return true;
         } else {
            logger.warn("Group {} cannot be deleted : group contains users", name);
         }
      }

      return false;
   }

   @Override
   @Transactional
   public UserGroup get(String name) {
      Optional<UserGroup> result = groupRepository.findByName(name);
      if(result.isPresent()) {
         logger.debug("Query for {} group received", name);
         return result.get();
      } else {
         logger.debug("Group {} not found", name);
         return null;
      }
   }

   @Override
   @Transactional
   public Iterable<UserGroup> getAll() {
      return groupRepository.findAll();
   }
}
