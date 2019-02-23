package classes.beans;

import com.kosprov.jargon2.api.Jargon2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;
import static com.kosprov.jargon2.api.Jargon2.jargon2Verifier;

public class PasswordHandler {

   static final private Logger logger = LoggerFactory.getLogger(PasswordHandler.class);
   static private final Jargon2.Hasher hasher = jargon2Hasher()
           .type(Jargon2.Type.ARGON2d)
           .memoryCost(65536)
           .timeCost(3)
           .parallelism(4)
           .hashLength(16);
   static private final Jargon2.Verifier verifier  = jargon2Verifier();


   static public String encode(String password){

       if(password != null && password.equals("")){
            byte[] passwordInBytes = password.getBytes();
            return hasher.password(passwordInBytes).encodedHash();
       } else {
           logger.debug("something went wrong : password = " + password);
           return null;
       }

   }


   public static boolean verifyPassword(String password, String encodedHash) throws IllegalArgumentException {
       if(password == null || encodedHash == null || password.equals("") || encodedHash.equals("")){
           throw new IllegalArgumentException("password or encodeedHash == null or == '' " + " " +
                   "==> password :" + password + "  encodedHash :" + encodedHash );
       }

         byte[] passwordInBytes = password.getBytes();
         return verifier.hash(encodedHash).password(passwordInBytes).verifyEncoded();
   }

}
