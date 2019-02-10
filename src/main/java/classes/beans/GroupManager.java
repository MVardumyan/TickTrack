package classes.beans;

import classes.entities.Group;
import classes.entities.User;
import classes.interfaces.IGroupManager;
import classes.repositories.GroupRepository;

import java.util.List;
import java.util.Optional;


public final class GroupManager implements IGroupManager {
   private GroupRepository groupRepository;

   @Override
   public void create(String name) {
      Group group = new Group();
      group.setName(name);
      groupRepository.save(group);
   }

   @Override
   public void migrateTo(String name, List<User> members) {
      Group group = new Group(name, members);
      groupRepository.save(group);
   }

   @Override
   public void changeName(String oldName, String newName) {
      groupRepository.findById(oldName).ifPresent(group -> {
         group.setName(newName);
         groupRepository.save(group);
      });
   }

   @Override
   public Group get(String name) {
      Optional<Group> byId = groupRepository.findById(name);
      return byId.get();
   }

   @Override
   public Iterable<Group> getAll() {
      return groupRepository.findAll();
   }
}
