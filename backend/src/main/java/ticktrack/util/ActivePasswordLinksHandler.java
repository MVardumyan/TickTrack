package ticktrack.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import ticktrack.entities.PasswordLink;
import ticktrack.repositories.PasswordLinkRepository;
import ticktrack.tasks.InvalidatePasswordLinkTask;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import javax.inject.Provider;

/**
 * Class stores all scheduled tasks for change password link removals in HashMap.
 * Key of HashMap is username, value - Scheduled task for link removal
 * Provides methods for adding and removing (canceling) tasks
 */
@Component
@Scope("singleton")
public class ActivePasswordLinksHandler {
    private HashMap<String, ScheduledFuture> tasks = new HashMap<>();
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final PasswordLinkRepository passwordLinkRepository;
    private final Provider<InvalidatePasswordLinkTask> taskProvider;
    private Logger logger = LoggerFactory.getLogger(ActivePasswordLinksHandler.class);

    @Autowired
    public ActivePasswordLinksHandler(ThreadPoolTaskScheduler threadPoolTaskScheduler,
                                      PasswordLinkRepository passwordLinkRepository,
                                      Provider<InvalidatePasswordLinkTask> taskProvider) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.passwordLinkRepository = passwordLinkRepository;
        this.taskProvider = taskProvider;
    }

    /**
     * Method creates InvalidatePasswordLinkTask and schedules it;
     * adds ScheduledFuture to tasks HashMap
     * @param passwordLink Entity used for providing it to InvalidatePasswordLinkTask entity
     *                     and getting username for tasks HashMap
     */
    public void addTask(PasswordLink passwordLink) {
        InvalidatePasswordLinkTask newPasswordLinkTask = taskProvider.get();
        newPasswordLinkTask.setPasswordLink(passwordLink);
        ScheduledFuture<?> task = threadPoolTaskScheduler.schedule(newPasswordLinkTask, passwordLink.getValidDate());

        tasks.put(passwordLink.getUser().getUsername(), task);

        logger.debug("Password link handling task for user {} is scheduled",
                passwordLink.getUser().getUsername());
    }

    /**
     * Method removes scheduled task from tasks HashMap;
     * cancels corresponding ScheduledFuture if it was found
     * @param passwordLink used to get ScheduledFuture from HashMap by username
     */
    public void removeTask(PasswordLink passwordLink) {
        ScheduledFuture task = tasks.remove(passwordLink.getUser().getUsername());

        if(task!=null) {
            task.cancel(false);
            logger.debug("Password link handling task for user {} is canceled",
                    passwordLink.getUser().getUsername());
        }
    }

    /**
     * Method used on application restart to restore all tasks
     */
    public void checkAllPasswordLinks() {
        passwordLinkRepository.findAll()
                .forEach(this::addTask);

        logger.debug("Restart job for Change Password links check done.");
    }
}
