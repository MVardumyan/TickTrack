package classes.beans;

import com.kosprov.jargon2.api.Jargon2;

import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;
import static com.kosprov.jargon2.api.Jargon2.jargon2Verifier;

public class PasswordHandler {

   static Jargon2.Hasher hasher = jargon2Hasher()
           .type(Jargon2.Type.ARGON2d)
           .memoryCost(65536)
           .timeCost(3)
           .parallelism(4)
           .hashLength(16);
   static private Jargon2.Verifier verifier  = jargon2Verifier();


   public static boolean verifyPassword(String password, String encodedHash) {
      if(password != null && encodedHash != null) {
         byte[] passwordInBytes = password.getBytes();
         return verifier.hash(encodedHash).password(passwordInBytes).verifyEncoded();
      } else {
         return false;
      }
   }
}
