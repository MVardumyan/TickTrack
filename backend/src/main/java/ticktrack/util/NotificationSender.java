package ticktrack.util;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NotificationSender {

    private final Configuration configuration = new Configuration()
            .domain("sandboxc83b27d7d2594ec09b6f6219d9ca8142.mailgun.org")
            .apiKey("57abafda584c725bfad3b6a590d52eed-de7062c6-4abd3140")
            .from("TickTrack", "Info@TickTrack.am");

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public boolean isValidMail(String emailStr) {
        if (emailStr != null) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
            return matcher.find();
        } else {
            return false;
        }
    }

    //before using this function address have to be checked with isValidMail
    public boolean sendMail(String address, String text) throws IllegalArgumentException {

        if (text == null || text.equals("")) {
            throw new IllegalArgumentException("Something went wrong : text = " + text);
        }

        return Mail.using(configuration)
                .to(address)
                .subject("TickTrack notification")
                .text(text)
                .build()
                .send().isOk();
    }

}
