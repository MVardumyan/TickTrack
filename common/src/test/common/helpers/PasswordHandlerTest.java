package common.helpers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class PasswordHandlerTest {

    private static String userPassword;
    private static List<String> falsePasswords = new ArrayList<String>();
    private static List<String> excpectedExceptionPasswords = new ArrayList<>();

    @BeforeEach()
     void setUp(){
        userPassword = "C@ts-and-Dogs-Living-together";
        falsePasswords.add(" ");
        falsePasswords.add("asdsafaagsdg");
        falsePasswords.add("        afdasfdasdf-adf      ");
        falsePasswords.add("  C@ts-and-Dogs-Living-together");


        excpectedExceptionPasswords.add("");
        excpectedExceptionPasswords.add(null);
    }

    @AfterEach
     void tearDown(){
        userPassword = null;
        falsePasswords.clear();
        excpectedExceptionPasswords.clear();

    }

    @Test
     void checkPasswords(){
        String encodeHash = PasswordHandler.encode(userPassword);
        assertTrue(PasswordHandler.verifyPassword(userPassword, encodeHash));
    }


    @Test
     void checkFalsePassword(){
        for(String password : falsePasswords){
            String encodeHash = PasswordHandler.encode(password);
            assertFalse(PasswordHandler.verifyPassword(userPassword, encodeHash));
        }
    }

    @Test
     void testExpectedException(){
        for(String password : excpectedExceptionPasswords){
            String encodeHash = PasswordHandler.encode(password);
            Assertions.assertThrows(IllegalArgumentException.class, ()-> {
                PasswordHandler.verifyPassword(userPassword, encodeHash);
            });
        }
    }


}
