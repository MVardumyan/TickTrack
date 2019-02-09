package classes.classes;


import java.util.List;

public class Group{
   String name;
   List<User> members;

   Group(String name,List<User> members){
      this.name = name;
      this.members = members;
   }
}
