package rocks.metaldetector.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.user.RefreshTokenEntity;
import rocks.metaldetector.persistence.domain.user.RefreshTokenRepository;
import rocks.metaldetector.support.SecurityProperties;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.util.concurrent.TimeUnit.DAYS;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupScheduler {

  private final RefreshTokenRepository refreshTokenRepository;
  private final SecurityProperties securityProperties;

  @Transactional
  @Scheduled(fixedRate = 1, timeUnit = DAYS)
  public void cleanupExpiredRefreshTokens() {
    log.info("Starting refresh token cleanup task...");
    LocalDateTime threshold = LocalDateTime.now().minusMinutes(securityProperties.getRefreshTokenExpirationInMin());
    Date thresholdAsDate = Date.from(threshold.atZone(ZoneId.systemDefault()).toInstant());
    List<RefreshTokenEntity> refreshTokens = refreshTokenRepository.findAllByLastModifiedDateTimeBefore(thresholdAsDate);

    if (!refreshTokens.isEmpty()) {
      log.info("Deleting {} expired refresh tokens", refreshTokens.size());
      refreshTokenRepository.deleteAll(refreshTokens);
    }
  }
}
