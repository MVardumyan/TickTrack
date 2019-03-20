package ticktrack.tasks;

import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ticktrack.entities.Ticket;
import ticktrack.repositories.TicketRepository;
import ticktrack.util.NotificationSender;

/**
 * Runnable class handles mail notification to ticket's assignee, if deadline will expire after 1 day.
 * run() methods queries all ticket's, which "deadline" field is not null, checks if assignee is not null;
 * checks that difference between current date and deadline equals 1 day and sends notification to assignee.
 */
@Component
public class CheckDeadlineTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CheckDeadlineTask.class);
    private final TicketRepository ticketRepository;
    private final NotificationSender notificationSender;

    @Autowired
    public CheckDeadlineTask(TicketRepository ticketRepository, NotificationSender notificationSender) {
        this.ticketRepository = ticketRepository;
        this.notificationSender = notificationSender;
    }

    @Override
    public void run() {
        LocalDate currentDate = new LocalDate(DateTimeZone.forID("Asia/Yerevan"));

        Iterable<Ticket> tickets = ticketRepository.findAllByDeadlineNotNull();
        for (Ticket ticket : tickets) {
            if(ticket.getAssignee()!=null) {
                LocalDate deadline = new LocalDate(ticket.getDeadline());
                if (Days.daysBetween(currentDate, deadline).getDays() == 1) {

                    String message = String.format("Hi %s!\nTicket %s deadline will expire at %s. Take a look:\n%s",
                            ticket.getAssignee().getUsername(),
                            ticket.getID(),
                            deadline,
                            "http://localhost:9203/ticketInfo/" + ticket.getID());

                    boolean notificationSent = notificationSender.sendMail(ticket.getAssignee().getEmail(), message);
                    if (notificationSent) {
                        logger.debug("Notification to {} about ticket {} deadline expiration sent",
                                ticket.getAssignee().getUsername(),
                                ticket.getID());
                    } else {
                        logger.warn("Notification to {} about ticket {} deadline expiration was not sent",
                                ticket.getAssignee().getUsername(),
                                ticket.getID());
                    }
                }
            }
        }

        logger.debug("Daily job for Deadline Check done.");
    }
}
