package classes.beans;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationSender {

    private static final Configuration configuration = new Configuration()
            .domain("sandbox640825bd39064eb58cc7d62b2de57f84.mailgun.org")
            .apiKey("key-cf1bb82f5ef89c6a8aba2a56c057d931-1b65790d-9b99b4f0")
            .from("TickTrack", "TickTrack@gmail.com");

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);



    public static boolean isValidMail(String emailStr) {
        if(emailStr != null) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
            return matcher.find();
        } else {
            return false;
        }
    }

    //use after using isValidMail
    public static boolean sendMail(String address, String text){
        if(text != null){
            return Mail.using(configuration)
                    .to(address)
                    .subject("TickTrack notification")
                    .text(text)
                    .build()
                    .send().isOk();
        } else {
            return false;
        }
    }

}
