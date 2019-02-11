package classes.beans;

import classes.entities.UserGroup;
import classes.entities.User;
import classes.interfaces.IUserGroupManager;
import classes.repositories.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class UserGroupManager implements IUserGroupManager {
   @Autowired
   private GroupRepository groupRepository;
   private Logger logger = LoggerFactory.getLogger(UserGroupManager.class);

   @Override
   @Transactional
   public void create(String name) {
      UserGroup group = new UserGroup();
      group.setName(name);
      groupRepository.save(group);
   }

   @Override
   @Transactional
   public void changeName(String oldName, String newName) {
      groupRepository.findById(oldName).ifPresent(group -> {
         group.setName(newName);
         groupRepository.save(group);
      });
   }

   @Override
   @Transactional
   public UserGroup get(String name) {
      Optional<UserGroup> byId = groupRepository.findById(name);
      return byId.get();
   }

   @Override
   @Transactional
   public Iterable<UserGroup> getAll() {
      return groupRepository.findAll();
   }
}
