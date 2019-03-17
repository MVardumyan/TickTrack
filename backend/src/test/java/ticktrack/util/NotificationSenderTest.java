package ticktrack.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NotificationSenderTest {

    private static List<String> validEmails = new ArrayList<>();
    private static List<String> invalidEmails = new ArrayList<>();
    @Autowired
    private NotificationSender notificationSender;


    @BeforeEach
    void steUp() {
        validEmails.add("davetisyan24106@gmail.com");
        validEmails.add("davetisyan114@gmail.com");
        validEmails.add("email@domain.name");


        invalidEmails.add("asdas");
        invalidEmails.add("asdasdasdsd   asdasdasd a asd as @ asd.com");
        invalidEmails.add("               ");
    }

    @AfterEach
    void tearDown() {
        validEmails.clear();
        invalidEmails.clear();
    }


    @Test
    void checkEmails() {
        for (String email : validEmails) {
            assertTrue(notificationSender.isValidMail(email));
        }

        for (String email : invalidEmails) {
            assertFalse(notificationSender.isValidMail(email));
        }
    }


    @Test
    void sendEmailTest() {
        assertTrue(notificationSender.sendMail(validEmails.get(0), "hello"));
        assertTrue(notificationSender.sendMail(validEmails.get(0), "    "));
        assertTrue(notificationSender.sendMail(validEmails.get(0), "hello asdfaslkfdjhakdsjlfhkajdfhkalhf ahdf "));
    }

    @Test
    void expectedExceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            notificationSender.sendMail(validEmails.get(0), "");
            assertFalse(notificationSender.sendMail(validEmails.get(0), null));
        });
    }

}
