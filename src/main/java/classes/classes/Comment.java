package classes.classes;

import java.sql.Timestamp;

public class Comment {
   String username;
   Timestamp timestamp;
   String text;

   Comment(String username, Timestamp timestamp, String text){
      this.username = username;
      this.timestamp = timestamp;
      this.text = text;
   }
}
