package rocks.metaldetector.service.cleanup;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.token.TokenRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class RegistrationCleanupServiceImpl implements RegistrationCleanupService {

  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final NotificationConfigRepository notificationConfigRepository;

  @Override
  @Transactional
  @Scheduled(cron = "0 0 0 * * *")
  public void cleanupUsersWithExpiredToken() {
    List<AbstractUserEntity> usersWithExpiredTokens = userRepository.findAllWithExpiredToken();
    if (!usersWithExpiredTokens.isEmpty()) {
      tokenRepository.deleteAllByUserIn(usersWithExpiredTokens);
      notificationConfigRepository.deleteAllByUserIn(usersWithExpiredTokens);
      userRepository.deleteAll(usersWithExpiredTokens);
    }
  }
}
