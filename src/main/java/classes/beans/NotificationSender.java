package classes.beans;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;

public class NotificationSender {

   public static boolean sendMail(String address, String text){
       Configuration configuration = new Configuration()
               .domain("sandbox640825bd39064eb58cc7d62b2de57f84.mailgun.org")
               .apiKey("cf1bb82f5ef89c6a8aba2a56c057d931-1b65790d-9b99b4f0")
               .from("TickTrack", "TickTrack@gmail.com");

       return Mail.using(configuration)
               .to(address)
               .subject("TickTrack notification")
               .text(text)
               .build()
               .send().isOk();
   }
}
