package ticktrack.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ticktrack.entities.PasswordLink;
import ticktrack.entities.User;
import ticktrack.repositories.PasswordLinkRepository;
import ticktrack.repositories.UserRepository;

import javax.transaction.Transactional;

/**
 * Runnable class handles change password link deletion from db, if validity period expired
 */
@Component
@Scope("prototype")
public class InvalidatePasswordLinkTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(InvalidatePasswordLinkTask.class);
    private final PasswordLinkRepository passwordLinkRepository;
    private final UserRepository userRepository;
    private PasswordLink passwordLink;

    @Autowired
    public InvalidatePasswordLinkTask(PasswordLinkRepository passwordLinkRepository, UserRepository userRepository) {
        this.passwordLinkRepository = passwordLinkRepository;
        this.userRepository = userRepository;
    }

    public void setPasswordLink(PasswordLink passwordLink) {
        this.passwordLink = passwordLink;
    }

    @Transactional
    @Override
    public void run() {
        if(passwordLink==null) {
            logger.warn("Unable to execute task : PasswordLink instance is null");
        } else {
            User user = passwordLink.getUser();
            passwordLinkRepository.delete(passwordLink);
            user.setPasswordLink(null);
            userRepository.save(user);

            logger.debug("Password Link for user {} removed", user.getUsername());
        }
    }
}
