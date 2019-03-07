package common.helpers;

import com.kosprov.jargon2.api.Jargon2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;
import static com.kosprov.jargon2.api.Jargon2.jargon2Verifier;

public class PasswordHandler {

    private static final Logger logger = LoggerFactory.getLogger(PasswordHandler.class);
    private static final Jargon2.Hasher hasher = jargon2Hasher()
            .type(Jargon2.Type.ARGON2d)
            .memoryCost(65536)
            .timeCost(3)
            .parallelism(4)
            .hashLength(16);
    private static final Jargon2.Verifier verifier = jargon2Verifier();


    public static String encode(String password) {

        if (password == null || password.equals("")) {
            logger.debug("something went wrong : password = " + password);
            return null;
        } else {
            byte[] passwordInBytes = password.getBytes();
            return hasher.password(passwordInBytes).encodedHash();
        }

    }

    public static boolean verifyPassword(String password, String encodedHash) throws IllegalArgumentException {
        if (password == null || encodedHash == null || password.equals("") || encodedHash.equals("")) {
            throw new IllegalArgumentException("password or encoded Hash == null or == '' " + " " +
                    "==> password :" + password + "  encodedHash :" + encodedHash);
        }

        byte[] passwordInBytes = password.getBytes();
        return verifier.hash(encodedHash).password(passwordInBytes).verifyEncoded();
    }

}
