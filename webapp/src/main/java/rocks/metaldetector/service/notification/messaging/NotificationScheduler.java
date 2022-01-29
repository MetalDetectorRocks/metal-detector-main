package rocks.metaldetector.service.notification.messaging;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.WEEKS;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Component
@AllArgsConstructor
public class NotificationScheduler {

  protected static final AnonymousAuthenticationToken PRINCIPAL = new AnonymousAuthenticationToken("key", "anonymous", createAuthorityList("ROLE_ANONYMOUS"));

  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationReleaseCollector notificationReleaseCollector;
  private final NotificationSenderSupplier notificationSenderSupplier;

  @Scheduled(cron = "0 0 7 * * SUN")
  @Transactional
  public void notifyOnFrequency() {
    var now = LocalDate.now();
    setSecurityContext(PRINCIPAL);
    try {
      notificationConfigRepository.findAllActive().stream()
          .filter(config -> notificationIsDue(config, now))
          .forEach(config -> frequencyNotification(config, now));
    }
    finally {
      setSecurityContext(null);
    }
  }

  @Scheduled(cron = "0 0 7 * * *")
  @Transactional
  public void notifyOnReleaseDate() {
    setSecurityContext(PRINCIPAL);
    try {
      notificationConfigRepository.findAllActive().stream()
          .filter(NotificationConfigEntity::getNotificationAtReleaseDate)
          .forEach(this::releaseDateNotification);
    }
    finally {
      setSecurityContext(null);
    }
  }

  @Scheduled(cron = "0 0 7 * * *")
  @Transactional
  public void notifyOnAnnouncementDate() {
    setSecurityContext(PRINCIPAL);
    try {
      notificationConfigRepository.findAllActive().stream()
          .filter(NotificationConfigEntity::getNotificationAtAnnouncementDate)
          .forEach(this::announcementDateNotification);
    }
    finally {
      setSecurityContext(null);
    }
  }

  private void frequencyNotification(NotificationConfigEntity notificationConfig, LocalDate now) {
    NotificationReleaseCollector.ReleaseContainer releaseContainer = notificationReleaseCollector.fetchReleasesForUserAndFrequency(notificationConfig.getUser(), notificationConfig.getFrequencyInWeeks(), notificationConfig.getNotifyReissues());

    if (!(releaseContainer.getUpcomingReleases().isEmpty() && releaseContainer.getRecentReleases().isEmpty())) {
      NotificationSender notificationSender = notificationSenderSupplier.apply(notificationConfig.getChannel());
      notificationSender.sendFrequencyMessage(notificationConfig.getUser(), releaseContainer.getUpcomingReleases(), releaseContainer.getRecentReleases());
    }

    notificationConfig.setLastNotificationDate(now);
    notificationConfigRepository.save(notificationConfig);
  }

  private void releaseDateNotification(NotificationConfigEntity notificationConfig) {
    List<ReleaseDto> todaysReleases = notificationReleaseCollector.fetchTodaysReleaseForUser(notificationConfig.getUser(), notificationConfig.getNotifyReissues());

    if (!todaysReleases.isEmpty()) {
      NotificationSender notificationSender = notificationSenderSupplier.apply(notificationConfig.getChannel());
      notificationSender.sendReleaseDateMessage(notificationConfig.getUser(), todaysReleases);
    }
  }

  private void announcementDateNotification(NotificationConfigEntity notificationConfig) {
    List<ReleaseDto> todaysAnnouncements = notificationReleaseCollector.fetchTodaysAnnouncementsForUser(notificationConfig.getUser(), notificationConfig.getNotifyReissues());

    if (!todaysAnnouncements.isEmpty()) {
      NotificationSender notificationSender = notificationSenderSupplier.apply(notificationConfig.getChannel());
      notificationSender.sendAnnouncementDateMessage(notificationConfig.getUser(), todaysAnnouncements);
    }
  }

  private boolean notificationIsDue(NotificationConfigEntity notificationConfig, LocalDate now) {
    return notificationConfig.getFrequencyInWeeks() > 0 &&
           (notificationConfig.getLastNotificationDate() == null ||
            WEEKS.between(notificationConfig.getLastNotificationDate(), now) >= notificationConfig.getFrequencyInWeeks());
  }

  private void setSecurityContext(Authentication authentication) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(authentication);
  }
}
