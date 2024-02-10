package rocks.metaldetector.service.notification.messaging;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.telegram.TelegramService;

import java.util.List;

@Service
@AllArgsConstructor
public class TelegramNotificationSender implements NotificationSender {

  static final String TODAYS_RELEASES_TEXT = "Today's metal releases:";
  static final String TODAYS_ANNOUNCEMENTS_TEXT = "Today's metal release announcements:";

  private final TelegramNotificationFormatter telegramNotificationFormatter;
  private final TelegramService telegramService;

  @Override
  @Transactional(readOnly = true)
  public void sendFrequencyMessage(AbstractUserEntity user, List<ReleaseDto> upcomingReleases, List<ReleaseDto> recentReleases) {
    String message = telegramNotificationFormatter.formatFrequencyNotificationMessage(upcomingReleases, recentReleases);
    telegramService.sendMessage(user, message);
  }

  @Override
  @Transactional(readOnly = true)
  public void sendReleaseDateMessage(AbstractUserEntity user, List<ReleaseDto> todaysReleases) {
    sendDateMessage(user, todaysReleases, TODAYS_RELEASES_TEXT);
  }

  @Override
  @Transactional(readOnly = true)
  public void sendAnnouncementDateMessage(AbstractUserEntity user, List<ReleaseDto> todaysAnnouncements) {
    sendDateMessage(user, todaysAnnouncements, TODAYS_ANNOUNCEMENTS_TEXT);
  }

  private void sendDateMessage(AbstractUserEntity user, List<ReleaseDto> releases, String releasesText) {
    String message = telegramNotificationFormatter.formatDateNotificationMessage(releases, releasesText);
    telegramService.sendMessage(user, message);
  }
}
