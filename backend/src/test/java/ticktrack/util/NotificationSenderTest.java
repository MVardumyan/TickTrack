package ticktrack.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ticktrack.util.NotificationSender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class NotificationSenderTest {

    private static List<String> validEmails = new ArrayList<>();
    private static List<String> invalidEmails = new ArrayList<>();


    @BeforeEach
     void steUp(){
        validEmails.add("davetisyan24106@gmail.com");
        validEmails.add("davetisyan114@gmail.com");
        validEmails.add("email@domain.name");


        invalidEmails.add("asdas");
        invalidEmails.add("asdasdasdsd   asdasdasd a asd as @ asd.com");
        invalidEmails.add("               ");
    }

    @AfterEach
     void tearDown(){
        validEmails.clear();
        invalidEmails.clear();
    }


   @Test
    void checkEmails(){
        for(String email : validEmails){
            assertTrue(NotificationSender.isValidMail(email));
        }

        for(String email : invalidEmails){
            assertFalse(NotificationSender.isValidMail(email));
        }
    }


    @Test
     void sendEmailTest(){
        assertTrue(NotificationSender.sendMail(validEmails.get(0), "hello"));
        assertTrue(NotificationSender.sendMail(validEmails.get(0), "    "));
        assertTrue(NotificationSender.sendMail(validEmails.get(0), "hello asdfaslkfdjhakdsjlfhkajdfhkalhf ahdf "));
    }

    @Test
     void excpectedExceptionTest(){
        Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            NotificationSender.sendMail(validEmails.get(0), "");
            assertFalse(NotificationSender.sendMail(validEmails.get(0), null));
        });
    }


}
