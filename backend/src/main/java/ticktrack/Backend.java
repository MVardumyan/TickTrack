package ticktrack;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ticktrack.tasks.CheckDeadlineTask;
import ticktrack.util.ActivePasswordLinksHandler;

@SpringBootApplication
public class Backend {

    @Bean
    @Scope("singleton")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "RegularTaskScheduler");
        return threadPoolTaskScheduler;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Backend.class, args);

        configureDeadlineScheduledTasks(context);
        configureChangePasswordLinkTasks(context);
    }

    /**
     * method starts periodic tasks for
     * deadline expiration notification (with start time - 24:00 and period - 1 day)
     * @param context Spring application context needed to get scheduler and task beans
     */
    private static void configureDeadlineScheduledTasks(ConfigurableApplicationContext context) {
        ThreadPoolTaskScheduler deadlineScheduler = context.getBean(ThreadPoolTaskScheduler.class);

        long period = 1000*60*60*24L; //1 day in milliseconds

        DateTime currentTime = DateTime.now().withZone(DateTimeZone.forID("Asia/Yerevan"));
        DateTime startTime = new DateTime(currentTime)
                .plusDays(1)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withZone(DateTimeZone.forID("Asia/Yerevan"));

        deadlineScheduler.scheduleAtFixedRate(context.getBean(CheckDeadlineTask.class), startTime.toDate(), period);
    }

    /**
     * method schedules tasks for password change link expiration (in case of system restart)
     * @param context Spring application context needed to get ActivePasswordLinksHandler beans
     */
    private static void configureChangePasswordLinkTasks(ConfigurableApplicationContext context) {
        ActivePasswordLinksHandler passwordLinksScheduler = context.getBean(ActivePasswordLinksHandler.class);

        passwordLinksScheduler.checkAllPasswordLinks();
    }

}