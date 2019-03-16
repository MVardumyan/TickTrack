package ticktrack.util;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationSender {

    private static final Configuration configuration = new Configuration()
            .domain("sandbox4ae2ffd66e7b4ee28828c8f8e525cca5.mailgun.org")
            .apiKey("")
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

    //before using this function address have to be checked with isValidMail
    public static boolean sendMail(String address, String text)throws IllegalArgumentException{

        if(text == null || text.equals("")){
            throw new IllegalArgumentException("Somthing went wrong : text = " + text);
        }

            return Mail.using(configuration)
                    .to(address)
                    .subject("TickTrack notification")
                    .text(text)
                    .build()
                    .send().isOk();
    }

}
