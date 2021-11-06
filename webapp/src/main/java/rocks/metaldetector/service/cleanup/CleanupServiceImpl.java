package rocks.metaldetector.service.cleanup;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.token.TokenRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CleanupServiceImpl implements CleanupService {

  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  @Scheduled(cron = "0 0 0 * * *")
  public void cleanupUsersWithExpiredToken() {
    List<UserEntity> userWithExpiredTokens = userRepository.findAllWithExpiredToken();
    if (!userWithExpiredTokens.isEmpty()) {
      tokenRepository.deleteAllByUserIn(userWithExpiredTokens);
      userRepository.deleteAll(userWithExpiredTokens);
    }
  }
}
