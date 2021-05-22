package rocks.metaldetector.service.notification.messaging;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigEntity;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.telegram.facade.TelegramMessagingService;

import java.util.List;

@Service
@AllArgsConstructor
public class TelegramNotificationSender implements NotificationSender {

  static final String TODAYS_RELEASES_TEXT = "Today's metal releases:";
  static final String TODAYS_ANNOUNCEMENTS_TEXT = "Today's metal release announcements:";

  private final TelegramMessagingService telegramMessagingService;
  private final TelegramConfigRepository telegramConfigRepository;
  private final TelegramNotificationFormatter telegramNotificationFormatter;

  @Override
  @Transactional(readOnly = true)
  public void sendFrequencyMessage(AbstractUserEntity user, List<ReleaseDto> upcomingReleases, List<ReleaseDto> recentReleases) {
    TelegramConfigEntity telegramConfig = telegramConfigRepository.findByUser(user).orElseThrow(
        () -> new ResourceNotFoundException("TelegramConfigEntity for user '" + user.getPublicId() + "' not found")
    );

    var chatId = telegramConfig.getChatId();
    if (chatId != null) {
      String message = telegramNotificationFormatter.formatFrequencyNotificationMessage(upcomingReleases, recentReleases);
      telegramMessagingService.sendMessage(chatId, message);
    }
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
    TelegramConfigEntity telegramConfig = telegramConfigRepository.findByUser(user).orElseThrow(
        () -> new ResourceNotFoundException("TelegramConfigEntity for user '" + user.getPublicId() + "' not found")
    );

    var chatId = telegramConfig.getChatId();
    if (chatId != null) {
      String message = telegramNotificationFormatter.formatDateNotificationMessage(releases, releasesText);
      telegramMessagingService.sendMessage(chatId, message);
    }
  }
}
