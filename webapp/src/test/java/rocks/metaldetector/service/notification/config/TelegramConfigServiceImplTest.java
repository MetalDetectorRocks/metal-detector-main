package rocks.metaldetector.service.notification.config;

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
import rocks.metaldetector.persistence.domain.notification.TelegramConfigEntity;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.telegram.facade.TelegramMessagingService;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;
import static rocks.metaldetector.service.notification.config.TelegramConfigServiceImpl.REGISTRATION_FAILED_ID_NOT_FOUND;
import static rocks.metaldetector.service.notification.config.TelegramConfigServiceImpl.REGISTRATION_FAILED_MESSAGE_NOT_READABLE;
import static rocks.metaldetector.service.notification.config.TelegramConfigServiceImpl.REGISTRATION_SUCCESSFUL_MESSAGE;

@ExtendWith(MockitoExtension.class)
class TelegramConfigServiceImplTest implements WithAssertions {

  @Mock
  private TelegramConfigRepository telegramConfigRepository;

  @Mock
  private TelegramConfigTransformer telegramConfigTransformer;

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private TelegramMessagingService telegramMessagingService;

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @InjectMocks
  private TelegramConfigServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(telegramConfigRepository, telegramConfigTransformer, authenticationFacade, telegramMessagingService, notificationConfigRepository);
  }

  @DisplayName("Tests for the fetching the configuration")
  @Nested
  class GetConfigurationTest {

    @Test
    @DisplayName("currentUserSupplier is called on fetching the config")
    void test_current_user_supplier_called() {
      // when
      underTest.getCurrentUserTelegramConfig();

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("repository is called on fetching the config")
    void test_repository_called() {
      // given
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(authenticationFacade).getCurrentUser();

      // when
      underTest.getCurrentUserTelegramConfig();

      // then
      verify(telegramConfigRepository).findByUser(user);
    }

    @Test
    @DisplayName("transformer is called if config is present")
    void test_transformer_called() {
      // given
      var config = TelegramConfigEntity.builder().build();
      doReturn(Optional.of(config)).when(telegramConfigRepository).findByUser(any());

      // when
      underTest.getCurrentUserTelegramConfig();

      // then
      verify(telegramConfigTransformer).transform(config);
    }

    @Test
    @DisplayName("transformer is not called if config is not present")
    void test_transformer_not_called() {
      // given
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());

      // when
      underTest.getCurrentUserTelegramConfig();

      // then
      verifyNoInteractions(telegramConfigTransformer);
    }

    @Test
    @DisplayName("dto is returned if config is present")
    void test_dto_returned() {
      // given
      var dto = TelegramConfigDto.builder().build();
      doReturn(dto).when(telegramConfigTransformer).transform(any());
      doReturn(Optional.of(TelegramConfigEntity.builder().build())).when(telegramConfigRepository).findByUser(any());

      // when
      var resultOptional = underTest.getCurrentUserTelegramConfig();

      // then
      assertThat(resultOptional).isPresent();
    }

    @Test
    @DisplayName("empty optional is returned if config is not present")
    void test_empty_optional_returned() {
      // given
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());

      // when
      var resultOptional = underTest.getCurrentUserTelegramConfig();

      // then
      assertThat(resultOptional).isEmpty();
    }
  }

  @DisplayName("Tests for the updating the configuration")
  @Nested
  class UpdateConfigurationTest {

    @Test
    @DisplayName("updateChatId: Updating telegram chat id calls telegramConfigRepository")
    void test_update_telegram_id_calls_repository() {
      // given
      var message = "666 ";

      // when
      underTest.updateChatId(message, 0);

      // then
      verify(telegramConfigRepository).findByRegistrationId(666);
    }

    @Test
    @DisplayName("updateChatId: Given telegram chat id is saved")
    void test_new_telegram_id_saved() {
      // given
      ArgumentCaptor<TelegramConfigEntity> argumentCaptor = ArgumentCaptor.forClass(TelegramConfigEntity.class);
      var chatId = 666;
      doReturn(Optional.of(TelegramConfigEntity.builder().build())).when(telegramConfigRepository).findByRegistrationId(anyInt());

      // when
      underTest.updateChatId("0", chatId);

      // then
      verify(telegramConfigRepository).save(argumentCaptor.capture());
      TelegramConfigEntity savedTelegramConfigEntity = argumentCaptor.getValue();

      assertThat(savedTelegramConfigEntity.getChatId()).isEqualTo(chatId);
      assertThat(savedTelegramConfigEntity.getRegistrationId()).isNull();
    }

    @Test
    @DisplayName("updateChatId: A confirmation message is sent to the chat id")
    void test_confirmation_sent() {
      // given
      var chatId = 666;
      doReturn(Optional.of(TelegramConfigEntity.builder().build())).when(telegramConfigRepository).findByRegistrationId(anyInt());

      // when
      underTest.updateChatId("0", chatId);

      // then
      verify(telegramMessagingService).sendMessage(chatId, REGISTRATION_SUCCESSFUL_MESSAGE);
    }

    @Test
    @DisplayName("updateChatId: A failure message is sent to the chat id if not telegram config could be found")
    void test_failure_message_sent() {
      // given
      var chatId = 666;
      var message = "555";
      doReturn(Optional.empty()).when(telegramConfigRepository).findByRegistrationId(anyInt());

      // when
      underTest.updateChatId(message, chatId);

      // then
      verify(telegramMessagingService).sendMessage(chatId, String.format(REGISTRATION_FAILED_ID_NOT_FOUND, 555));
    }

    @Test
    @DisplayName("updateChatId: repository is not called if message could not be parsed")
    void test_repository_not_called() {
      // given
      var message = "abc";

      // when
      underTest.updateChatId(message, 666);

      // then
      verifyNoInteractions(telegramConfigRepository);
    }

    @Test
    @DisplayName("updateChatId: failure message is sent if message could not be parsed")
    void test_failure_message_sent_paring_exception() {
      // given
      var chatId = 666;
      var message = "abc";

      // when
      underTest.updateChatId(message, chatId);

      // then
      verify(telegramMessagingService).sendMessage(chatId, String.format(REGISTRATION_FAILED_MESSAGE_NOT_READABLE, message));
    }

    @Test
    @DisplayName("generateRegistrationId: an unused registration id is generated")
    void test_unused_id_generated() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var usedId1 = 555_555;
      var usedId2 = 666_666;
      var unusedId = 777_777;
      doReturn(usedId1, usedId2, unusedId).when(threadLocalRandomMock).nextInt(anyInt(), anyInt());
      doReturn(true, true, false).when(telegramConfigRepository).existsByRegistrationId(any());
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.of(TelegramConfigEntity.builder().build())).when(telegramConfigRepository).findByUser(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(telegramConfigRepository).existsByRegistrationId(usedId1);
      verify(telegramConfigRepository).existsByRegistrationId(usedId2);
      verify(telegramConfigRepository).existsByRegistrationId(unusedId);
    }

    @Test
    @DisplayName("generateRegistrationId: currentUser is fetched")
    void test_current_user_fetched_on_id_generation() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.of(TelegramConfigEntity.builder().build())).when(telegramConfigRepository).findByUser(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("exception is thrown if after 100 retries no unique id was found")
    void test_exception_thrown() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      doReturn(true).when(telegramConfigRepository).existsByRegistrationId(any());

      // when
      Throwable throwable;
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        throwable = catchThrowable(() -> underTest.generateRegistrationId());
      }

      // then
      assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("generateRegistrationId: user's telegram config is fetched")
    void test_users_config_fetched_on_id_generation() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.of(TelegramConfigEntity.builder().build())).when(telegramConfigRepository).findByUser(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(telegramConfigRepository).findByUser(user);
    }

    @Test
    @DisplayName("generateRegistrationId: existing telegram notification config is fetched if no telegram config exists")
    void test_fetch_existing_telegram_notification_config() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(notificationConfigRepository).findByUserAndChannel(user, TELEGRAM);
    }

    @Test
    @DisplayName("generateRegistrationId: existing email notification config is fetched if no telegram config exists")
    void test_fetch_existing_email_notification_config() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserAndChannel(any(), eq(TELEGRAM));

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(notificationConfigRepository).findByUserAndChannel(user, EMAIL);
    }

    @Test
    @DisplayName("generateRegistrationId: default email notification config values are used if no email notification config telegram config exist (legacy for existing users without email config)")
    void test_default_email_notification_config() {
      // given
      ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserAndChannel(any(), eq(TELEGRAM));
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserAndChannel(any(), eq(EMAIL));

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(notificationConfigRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue().getNotificationAtAnnouncementDate()).isTrue();
      assertThat(argumentCaptor.getValue().getNotificationAtReleaseDate()).isTrue();
      assertThat(argumentCaptor.getValue().getFrequencyInWeeks()).isEqualTo(4);
      assertThat(argumentCaptor.getValue().getNotifyReissues()).isFalse();
    }

    @Test
    @DisplayName("generateRegistrationId: telegram notification config is created with values from email notification config if it does not exist")
    void test_create_new_notification_config() {
      // given
      ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      var emailNotificationConfig = NotificationConfigEntity.builder().build();
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserAndChannel(any(), eq(TELEGRAM));
      doReturn(Optional.of(emailNotificationConfig)).when(notificationConfigRepository).findByUserAndChannel(any(), eq(EMAIL));

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(notificationConfigRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue().getUser()).isEqualTo(user);
      assertThat(argumentCaptor.getValue().getChannel()).isEqualTo(TELEGRAM);
      assertThat(argumentCaptor.getValue().getNotificationAtAnnouncementDate()).isEqualTo(emailNotificationConfig.getNotificationAtAnnouncementDate());
      assertThat(argumentCaptor.getValue().getNotificationAtReleaseDate()).isEqualTo(emailNotificationConfig.getNotificationAtReleaseDate());
      assertThat(argumentCaptor.getValue().getFrequencyInWeeks()).isEqualTo(emailNotificationConfig.getFrequencyInWeeks());
      assertThat(argumentCaptor.getValue().getNotifyReissues()).isEqualTo(emailNotificationConfig.getNotifyReissues());
    }

    @Test
    @DisplayName("generateRegistrationId: newly created telegram config is saved")
    void test_newly_created_telegram_config_is_saved() {
      // given
      ArgumentCaptor<TelegramConfigEntity> argumentCaptor = ArgumentCaptor.forClass(TelegramConfigEntity.class);
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      var notificationConfig = mock(NotificationConfigEntity.class);
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());
      doReturn(Optional.of(notificationConfig)).when(notificationConfigRepository).findByUserAndChannel(any(), any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(telegramConfigRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue().getNotificationConfig()).isEqualTo(notificationConfig);
    }

    @Test
    @DisplayName("generateRegistrationId: generated id is saved on new config")
    void test_generated_id_saved_new_config() {
      // given
      ArgumentCaptor<TelegramConfigEntity> argumentCaptor = ArgumentCaptor.forClass(TelegramConfigEntity.class);
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var id = 666_666;
      doReturn(id).when(threadLocalRandomMock).nextInt(anyInt(), anyInt());
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(telegramConfigRepository).save(argumentCaptor.capture());
      TelegramConfigEntity notificationConfig = argumentCaptor.getValue();
      assertThat(notificationConfig.getRegistrationId()).isEqualTo(id);
    }

    @Test
    @DisplayName("generateRegistrationId: generated id is returned")
    void test_generated_id_returned() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var id = 666_666;
      doReturn(id).when(threadLocalRandomMock).nextInt(anyInt(), anyInt());
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.of(TelegramConfigEntity.builder().build())).when(telegramConfigRepository).findByUser(any());

      // when
      var result = 0;
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        result = underTest.generateRegistrationId();
      }

      // then
      assertThat(result).isEqualTo(id);
    }
  }

  @DisplayName("Tests for the deleting the configuration")
  @Nested
  class DeleteConfigurationTest {

    @Test
    @DisplayName("should get the current user")
    void should_get_the_current_user() {
      // when
      underTest.deleteCurrentUserTelegramConfig();

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("should delete the telegram config")
    void should_delete_the_telegram_config() {
      // given
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(authenticationFacade).getCurrentUser();

      // when
      underTest.deleteCurrentUserTelegramConfig();

      // then
      verify(telegramConfigRepository).deleteByUser(user);
    }

    @Test
    @DisplayName("should delete the notification config")
    void should_delete_the_notification_config() {
      // given
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(authenticationFacade).getCurrentUser();

      // when
      underTest.deleteCurrentUserTelegramConfig();

      // then
      verify(notificationConfigRepository).deleteByUserAndChannel(user, TELEGRAM);
    }
  }
}
