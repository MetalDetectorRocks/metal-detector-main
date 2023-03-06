package rocks.metaldetector.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;

import java.util.List;

import static java.util.concurrent.TimeUnit.DAYS;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationCleanupService {

  private final UserRepository userRepository;
  private final NotificationConfigRepository notificationConfigRepository;

  @Transactional
  @Scheduled(fixedRate = 1, timeUnit = DAYS)
  public void cleanupUsersWithExpiredToken() {
    log.info("Starting registration cleanup task...");
    List<AbstractUserEntity> usersWithExpiredTokens = userRepository.findAllExpiredUsers();
    if (!usersWithExpiredTokens.isEmpty()) {
      log.info("Deleting {} users who have not verified their registration within 10 days", usersWithExpiredTokens.size());
      notificationConfigRepository.deleteAllByUserIn(usersWithExpiredTokens);
      userRepository.deleteAll(usersWithExpiredTokens);
    }
  }
}
