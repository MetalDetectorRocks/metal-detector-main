package rocks.metaldetector.service.notification.messaging;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.WEEKS;

@Component
@AllArgsConstructor
public class NotificationScheduler {

  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationReleaseCollector notificationReleaseCollector;
  private final NotificationSenderSupplier notificationSenderSupplier;

  @Scheduled(cron = "0 0 4 * * SUN")
  @Transactional
  public void notifyOnFrequency() {
    var now = LocalDate.now();
    notificationConfigRepository.findAllActive().stream()
        .filter(config -> notificationIsDue(config, now))
        .forEach(config -> frequencyNotification(config, now));
  }

  @Scheduled(cron = "0 0 7 * * *")
  @Transactional(readOnly = true)
  public void notifyOnReleaseDate() {
    notificationConfigRepository.findAllActive().stream()
        .filter(NotificationConfigEntity::getNotificationAtReleaseDate)
        .forEach(this::releaseDateNotification);
  }

  @Scheduled(cron = "0 0 7 * * *")
  @Transactional(readOnly = true)
  public void notifyOnAnnouncementDate() {
    notificationConfigRepository.findAllActive().stream()
        .filter(NotificationConfigEntity::getNotificationAtAnnouncementDate)
        .forEach(this::announcementDateNotification);
  }

  private void frequencyNotification(NotificationConfigEntity notificationConfig, LocalDate now) {
    NotificationReleaseCollector.ReleaseContainer releaseContainer = notificationReleaseCollector.fetchReleasesForUserAndFrequency(notificationConfig.getUser(), notificationConfig.getFrequencyInWeeks());
    NotificationSender notificationSender = notificationSenderSupplier.apply(notificationConfig.getChannel());
    notificationSender.sendFrequencyMessage(notificationConfig.getUser(), releaseContainer.getUpcomingReleases(), releaseContainer.getRecentReleases());
    notificationConfig.setLastNotificationDate(now);
    notificationConfigRepository.save(notificationConfig);
  }

  private void releaseDateNotification(NotificationConfigEntity notificationConfig) {
    List<ReleaseDto> todaysReleases = notificationReleaseCollector.fetchTodaysReleaseForUser(notificationConfig.getUser());
    NotificationSender notificationSender = notificationSenderSupplier.apply(notificationConfig.getChannel());
    notificationSender.sendReleaseDateMessage(notificationConfig.getUser(), todaysReleases);
  }

  private void announcementDateNotification(NotificationConfigEntity notificationConfig) {
    List<ReleaseDto> todaysAnnouncements = notificationReleaseCollector.fetchTodaysAnnouncementsForUser(notificationConfig.getUser());
    NotificationSender notificationSender = notificationSenderSupplier.apply(notificationConfig.getChannel());
    notificationSender.sendAnnouncementDateMessage(notificationConfig.getUser(), todaysAnnouncements);
  }

  private boolean notificationIsDue(NotificationConfigEntity notificationConfig, LocalDate now) {
    return notificationConfig.getLastNotificationDate() == null ||
           WEEKS.between(notificationConfig.getLastNotificationDate(), now) >= notificationConfig.getFrequencyInWeeks();
  }
}
