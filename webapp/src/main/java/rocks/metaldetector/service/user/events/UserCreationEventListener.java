package rocks.metaldetector.service.user.events;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;

@AllArgsConstructor
@Component
public class UserCreationEventListener implements ApplicationListener<UserCreationEvent> {

  private final NotificationConfigRepository notificationConfigRepository;

  @Override
  @Transactional
  public void onApplicationEvent(UserCreationEvent event) {
    AbstractUserEntity user = event.getUserEntity();

    NotificationConfigEntity notificationConfig = NotificationConfigEntity.builder()
        .user(user)
        .channel(EMAIL)
        .build();
    notificationConfigRepository.save(notificationConfig);
  }
}
