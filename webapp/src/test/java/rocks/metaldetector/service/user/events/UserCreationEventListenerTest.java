package rocks.metaldetector.service.user.events;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.service.user.UserService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;

@ExtendWith(MockitoExtension.class)
class UserCreationEventListenerTest implements WithAssertions {

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @InjectMocks
  private UserCreationEventListener underTest;

  @AfterEach
  void tearDown() {
    reset(notificationConfigRepository);
  }

  @Test
  @DisplayName("notificationConfigRepository is called with default config")
  void test_notification_config_repo_called() {
    // given
    ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
    UserEntity user = UserEntityFactory.createUser("user", "user@user.user");
    NotificationConfigEntity expectedNotificationConfig = NotificationConfigEntity.builder()
        .user(user)
        .channel(EMAIL)
        .build();

    // when
    underTest.onApplicationEvent(new UserCreationEvent(mock(UserService.class), user));

    // then
    verify(notificationConfigRepository).save(argumentCaptor.capture());
    NotificationConfigEntity notificationConfig = argumentCaptor.getValue();
    assertThat(notificationConfig).isEqualTo(expectedNotificationConfig);
  }
}
