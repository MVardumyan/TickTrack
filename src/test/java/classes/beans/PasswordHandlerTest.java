package classes.beans;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordHandlerTest {

    private static String userPassword;
    private static List<String> falsePasswords = new ArrayList<String>();

    @BeforeEach()
    public void setUp(){
        userPassword = "C@ts-and-Dogs-Living-together";
        falsePasswords.add(" ");
        falsePasswords.add(null);
        falsePasswords.add("");
        falsePasswords.add("asdsafaagsdg");
        falsePasswords.add("        afdasfdasdf-adf      ");
        falsePasswords.add("  C@ts-and-Dogs-Living-together");

    }

    @AfterEach
    public void tearDown(){
        userPassword = null;
        falsePasswords.clear();

    }

    @Test
    public void checkTruePassword(){
        String encodeHash = PasswordHandler.encode(userPassword);
        assertTrue(PasswordHandler.verifyPassword(userPassword, encodeHash));
    }


    @Test
    public void checkFalsePassword(){
        for(String password : falsePasswords){
            String encodeHash = PasswordHandler.encode(password);
            assertFalse(PasswordHandler.verifyPassword(userPassword, encodeHash));
        }
    }


}
