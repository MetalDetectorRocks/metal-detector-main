package rocks.metaldetector.service.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationConfigServiceImpl implements NotificationConfigService {

  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationConfigTransformer notificationConfigTransformer;
  private final CurrentUserSupplier currentUserSupplier;

  @Override
  @Transactional(readOnly = true)
  public NotificationConfigDto getCurrentUserNotificationConfig() {
    AbstractUserEntity currentUser = currentUserSupplier.get();
    NotificationConfigEntity notificationConfigEntity = notificationConfigRepository.findByUserId(currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Notification config for user '" + currentUser.getPublicId() + "' not found"));
    return notificationConfigTransformer.transform(notificationConfigEntity);
  }

  @Override
  @Transactional
  public void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto) {
    AbstractUserEntity currentUser = currentUserSupplier.get();
    NotificationConfigEntity notificationConfigEntity = notificationConfigRepository.findByUserId(currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Notification config for user '" + currentUser.getPublicId() + "' not found"));

    notificationConfigEntity.setFrequencyInWeeks(notificationConfigDto.getFrequencyInWeeks());
    notificationConfigEntity.setNotificationAtAnnouncementDate(notificationConfigDto.isNotificationAtAnnouncementDate());
    notificationConfigEntity.setNotificationAtReleaseDate(notificationConfigDto.isNotificationAtReleaseDate());
    notificationConfigEntity.setNotify(notificationConfigDto.isNotify());

    notificationConfigRepository.save(notificationConfigEntity);
  }

  @Override
  @Transactional
  public void updateTelegramChatId(int registrationId, int chatId) {
    Optional<NotificationConfigEntity> notificationConfigOptional = notificationConfigRepository.findByTelegramRegistrationId(registrationId);
    if (notificationConfigOptional.isPresent()) {
      NotificationConfigEntity notificationConfig = notificationConfigOptional.get();
      notificationConfig.setTelegramChatId(chatId);
      notificationConfig.setTelegramRegistrationId(null);
      notificationConfigRepository.save(notificationConfig);
    } else {
      log.warn("Could not set telegram chat id for registration id '{}' - id not found", registrationId);
    }
  }

  @Override
  @Transactional
  public int generateTelegramRegistrationId() {
    int registrationId;
    do { registrationId = ThreadLocalRandom.current().nextInt(100_000, 1_000_000); }
    while (notificationConfigRepository.existsByTelegramRegistrationId(registrationId));

    AbstractUserEntity currentUser = currentUserSupplier.get();
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    NotificationConfigEntity notificationConfig = notificationConfigRepository.findByUserId(currentUser.getId()).get();
    notificationConfig.setTelegramRegistrationId(registrationId);
    notificationConfigRepository.save(notificationConfig);
    return registrationId;
  }

  @Override
  @Transactional
  public void deactivateTelegramNotifications() {
    AbstractUserEntity currentUser = currentUserSupplier.get();
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    NotificationConfigEntity notificationConfig = notificationConfigRepository.findByUserId(currentUser.getId()).get();
    notificationConfig.setTelegramChatId(null);
    notificationConfigRepository.save(notificationConfig);
  }
}
