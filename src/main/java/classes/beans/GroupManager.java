package classes.beans;

import classes.entities.Group;
import classes.entities.User;
import classes.interfaces.IGroupManager;
import classes.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupManager implements IGroupManager {
   @Autowired
   private GroupRepository groupRepository;

   @Override
   @Transactional
   public void create(String name) {
      Group group = new Group();
      group.setName(name);
      groupRepository.save(group);
   }

   @Override
   @Transactional
   public void migrateTo(String name, List<User> members) {
      Group group = new Group(name, members);
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
   public Group get(String name) {
      Optional<Group> byId = groupRepository.findById(name);
      return byId.get();
   }

   @Override
   @Transactional
   public Iterable<Group> getAll() {
      return groupRepository.findAll();
   }
}
