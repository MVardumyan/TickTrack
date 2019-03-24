package ticktrack.util;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NotificationSender {

    private final Configuration configuration = new Configuration()
            .domain("sandbox00ce7ea3ee2f4e39b0c86aeeadfccdc2.mailgun.org")
            .apiKey("")
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
