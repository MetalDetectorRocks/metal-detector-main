package rocks.metaldetector.service.notification.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.notification.NotificationChannel;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationConfigServiceImpl implements NotificationConfigService {

  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationConfigTransformer notificationConfigTransformer;
  private final CurrentUserSupplier currentUserSupplier;

  @Override
  @Transactional(readOnly = true)
  public List<NotificationConfigDto> getCurrentUserNotificationConfigs() {
    AbstractUserEntity currentUser = currentUserSupplier.get();
    List<NotificationConfigEntity> notificationConfigs = notificationConfigRepository.findAllByUser(currentUser);
    return notificationConfigs.stream()
        .map(notificationConfigTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfigDto) {
    AbstractUserEntity currentUser = currentUserSupplier.get();

    NotificationChannel channel = NotificationChannel.from(notificationConfigDto.getChannel());
    Optional<NotificationConfigEntity> notificationConfigOptional = notificationConfigRepository.findByUserAndChannel(currentUser, channel);
    NotificationConfigEntity notificationConfig = notificationConfigOptional.orElseGet(() -> NotificationConfigEntity.builder()
        .user(currentUser)
        .channel(channel)
        .build());

    notificationConfig.setFrequencyInWeeks(notificationConfigDto.getFrequencyInWeeks());
    notificationConfig.setNotificationAtAnnouncementDate(notificationConfigDto.isNotificationAtAnnouncementDate());
    notificationConfig.setNotificationAtReleaseDate(notificationConfigDto.isNotificationAtReleaseDate());
    notificationConfig.setNotify(notificationConfigDto.isNotify());
    notificationConfig.setNotifyReissues(notificationConfigDto.isNotifyReissues());

    notificationConfigRepository.save(notificationConfig);
  }
}
