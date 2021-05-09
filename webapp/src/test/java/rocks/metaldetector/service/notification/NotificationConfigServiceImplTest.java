package rocks.metaldetector.service.notification;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.telegram.facade.TelegramService;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.notification.NotificationConfigServiceImpl.REGISTRATION_SUCCESSFUL_MESSAGE;

@ExtendWith(MockitoExtension.class)
class NotificationConfigServiceImplTest implements WithAssertions {

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @Mock
  private NotificationConfigTransformer notificationConfigTransformer;

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private TelegramService telegramService;

  @InjectMocks
  private NotificationConfigServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(notificationConfigRepository, notificationConfigTransformer, currentUserSupplier,
          notificationConfigRepository, telegramService);
  }

  @DisplayName("Tests for getting notification config")
  @Nested
  class GetNotificationConfigTest {

    @Test
    @DisplayName("Getting current user's config calls currentUserSupplier")
    void test_get_config_calls_current_user_supplier() {
      // given
      UserEntity userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      doReturn(userEntity).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().user(userEntity).build())).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.getCurrentUserNotificationConfig();

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("Getting current user's config calls notificationConfigRepository")
    void test_get_config_calls_notification_repo() {
      // given
      var mockUser = mock(UserEntity.class);
      var userId = 666L;
      doReturn(userId).when(mockUser).getId();
      doReturn(mockUser).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().user(mockUser).build())).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.getCurrentUserNotificationConfig();

      // then
      verify(notificationConfigRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Getting current user's config throws exception when id not found")
    void test_get_config_throws_exception() {
      // given
      var mockUser = mock(UserEntity.class);
      var userId = 666L;
      var publicUserId = "123abc";
      doReturn(userId).when(mockUser).getId();
      doReturn(publicUserId).when(mockUser).getPublicId();
      doReturn(mockUser).when(currentUserSupplier).get();
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserId(any());

      // when
      var throwable = catchThrowable(() -> underTest.getCurrentUserNotificationConfig());

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(publicUserId);
    }

    @Test
    @DisplayName("Getting current user's config calls notificationConfigTransformer")
    void test_get_config_calls_notification_config_trafo() {
      // given
      var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      var notificationConfigEntity = NotificationConfigEntity.builder().user(userEntity).build();
      doReturn(userEntity).when(currentUserSupplier).get();
      doReturn(Optional.of(notificationConfigEntity)).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.getCurrentUserNotificationConfig();

      // then
      verify(notificationConfigTransformer).transform(notificationConfigEntity);
    }

    @Test
    @DisplayName("Getting current user's config returns dto")
    void test_get_config_returns_dto() {
      // given
      var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      var notificationConfigDto = NotificationConfigDto.builder().frequencyInWeeks(4).build();
      doReturn(userEntity).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().user(userEntity).build())).when(notificationConfigRepository).findByUserId(any());
      doReturn(notificationConfigDto).when(notificationConfigTransformer).transform(any());

      // when
      var result = underTest.getCurrentUserNotificationConfig();

      // then
      assertThat(result).isEqualTo(notificationConfigDto);
    }
  }

  @DisplayName("Tests for updating notification config")
  @Nested
  class UpdateNotificationConfigTest {

    @Test
    @DisplayName("Updating current user's config calls currentUserSupplier")
    void test_update_config_calls_current_user_supplier() {
      // given
      UserEntity userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      doReturn(userEntity).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().user(userEntity).build())).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.updateCurrentUserNotificationConfig(new NotificationConfigDto());

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("Updating current user's config calls notificationConfigRepository")
    void test_update_config_calls_notification_repo() {
      // given
      var mockUser = mock(UserEntity.class);
      var userId = 666L;
      doReturn(userId).when(mockUser).getId();
      doReturn(mockUser).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().user(mockUser).build())).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.updateCurrentUserNotificationConfig(new NotificationConfigDto());

      // then
      verify(notificationConfigRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Updating current user's config throws exception when id not found")
    void test_update_config_throws_exception() {
      // given
      var mockUser = mock(UserEntity.class);
      var userId = 666L;
      var publicUserId = "123abc";
      doReturn(userId).when(mockUser).getId();
      doReturn(publicUserId).when(mockUser).getPublicId();
      doReturn(mockUser).when(currentUserSupplier).get();
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserId(any());

      // when
      var throwable = catchThrowable(() -> underTest.updateCurrentUserNotificationConfig(new NotificationConfigDto()));

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(publicUserId);
    }

    @Test
    @DisplayName("Updated config is saved")
    void test_updated_config_saved() {
      // given
      ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
      var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      var notificationConfig = NotificationConfigEntity.builder().user(userEntity)
          .frequencyInWeeks(2)
          .build();
      var notificationConfigDto = NotificationConfigDto.builder()
          .frequencyInWeeks(4)
          .notify(true).build();
      doReturn(userEntity).when(currentUserSupplier).get();
      doReturn(Optional.of(notificationConfig)).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.updateCurrentUserNotificationConfig(notificationConfigDto);

      // then
      verify(notificationConfigRepository).save(argumentCaptor.capture());
      var savedEntity = argumentCaptor.getValue();
      assertThat(savedEntity.getUser()).isEqualTo(notificationConfig.getUser());
      assertThat(savedEntity.getFrequencyInWeeks()).isEqualTo(notificationConfigDto.getFrequencyInWeeks());
      assertThat(savedEntity.getNotify()).isEqualTo(notificationConfigDto.isNotify());
      assertThat(savedEntity.getNotificationAtReleaseDate()).isEqualTo(notificationConfigDto.isNotificationAtReleaseDate());
      assertThat(savedEntity.getNotificationAtAnnouncementDate()).isEqualTo(notificationConfigDto.isNotificationAtAnnouncementDate());
    }
  }

  @DisplayName("Tests for the telegram configuration")
  @Nested
  class TelegramConfigurationTest {

    @Test
    @DisplayName("updateTelegramChatId: Updating telegram chat id calls notificationConfigRepository")
    void test_update_telegram_id_calls_repository() {
      // given
      var registrationId = 666;

      // when
      underTest.updateTelegramChatId(registrationId, 0);

      // then
      verify(notificationConfigRepository).findByTelegramRegistrationId(registrationId);
    }

    @Test
    @DisplayName("updateTelegramChatId: Given telegram chat id is saved")
    void test_new_telegram_id_saved() {
      // given
      ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
      var chatId = 666;
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByTelegramRegistrationId(anyInt());

      // when
      underTest.updateTelegramChatId(0, chatId);

      // then
      verify(notificationConfigRepository).save(argumentCaptor.capture());
      NotificationConfigEntity savedNotificationConfigEntity = argumentCaptor.getValue();

      assertThat(savedNotificationConfigEntity.getTelegramChatId()).isEqualTo(chatId);
      assertThat(savedNotificationConfigEntity.getTelegramRegistrationId()).isNull();
    }

    @Test
    @DisplayName("updateTelegramChatId: A confirmation message is sent to the chat id")
    void test_confirmation_sent() {
      // given
      var chatId = 666;
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByTelegramRegistrationId(anyInt());

      // when
      underTest.updateTelegramChatId(0, chatId);

      // then
      verify(telegramService).sendMessage(chatId, REGISTRATION_SUCCESSFUL_MESSAGE);
    }

    @Test
    @DisplayName("generateTelegramRegistrationId: an unused registration id is generated")
    void test_unused_id_generated() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var usedId1 = 555_555;
      var usedId2 = 666_666;
      var unusedId = 777_777;
      doReturn(usedId1, usedId2, unusedId).when(threadLocalRandomMock).nextInt(anyInt(), anyInt());
      doReturn(true, true, false).when(notificationConfigRepository).existsByTelegramRegistrationId(any());
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByUserId(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateTelegramRegistrationId();
      }

      // then
      verify(notificationConfigRepository).existsByTelegramRegistrationId(usedId1);
      verify(notificationConfigRepository).existsByTelegramRegistrationId(usedId2);
      verify(notificationConfigRepository).existsByTelegramRegistrationId(unusedId);
    }

    @Test
    @DisplayName("generateTelegramRegistrationId: currentUser is fetched")
    void test_current_user_fetched_on_id_generation() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByUserId(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateTelegramRegistrationId();
      }

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("generateTelegramRegistrationId: user's notification config is fetched")
    void test_users_notification_config_fetched_on_id_generation() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByUserId(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateTelegramRegistrationId();
      }

      // then
      verify(notificationConfigRepository).findByUserId(user.getId());
    }

    @Test
    @DisplayName("generateTelegramRegistrationId: generated id is saved")
    void test_generated_id_saved() {
      // given
      ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var id = 666_666;
      doReturn(id).when(threadLocalRandomMock).nextInt(anyInt(), anyInt());
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByUserId(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateTelegramRegistrationId();
      }

      // then
      verify(notificationConfigRepository).save(argumentCaptor.capture());
      NotificationConfigEntity notificationConfig = argumentCaptor.getValue();

      assertThat(notificationConfig.getTelegramRegistrationId()).isEqualTo(id);
    }

    @Test
    @DisplayName("generateTelegramRegistrationId: generated id is returned")
    void test_generated_id_returned() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var id = 666_666;
      doReturn(id).when(threadLocalRandomMock).nextInt(anyInt(), anyInt());
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByUserId(any());

      // when
      var result = 0;
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        result = underTest.generateTelegramRegistrationId();
      }

      // then
      assertThat(result).isEqualTo(id);
    }

    @Test
    @DisplayName("deactivateTelegramNotifications: current user is fetched")
    void test_current_user_fetched_on_deactivation() {
      // given
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.deactivateTelegramNotifications();

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("deactivateTelegramNotifications: user's notification config is fetched")
    void test_users_notification_config_fetched_on_deactivation() {
      // given
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.deactivateTelegramNotifications();

      // then
      verify(notificationConfigRepository).findByUserId(user.getId());
    }

    @Test
    @DisplayName("deactivateTelegramNotifications: chat id is deleted")
    void test_chat_id_deleted() {
      // given
      ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
      doReturn(Optional.of(NotificationConfigEntity.builder().build())).when(notificationConfigRepository).findByUserId(any());

      // when
      underTest.deactivateTelegramNotifications();

      // then
      verify(notificationConfigRepository).save(argumentCaptor.capture());
      NotificationConfigEntity notificationConfig = argumentCaptor.getValue();

      assertThat(notificationConfig.getTelegramChatId()).isNull();
    }


  }
}
