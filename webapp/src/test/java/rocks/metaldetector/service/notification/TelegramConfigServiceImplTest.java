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
import rocks.metaldetector.persistence.domain.notification.TelegramConfigEntity;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.telegram.facade.TelegramMessagingService;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.service.notification.TelegramConfigServiceImpl.REGISTRATION_FAILED_ID_NOT_FOUND;
import static rocks.metaldetector.service.notification.TelegramConfigServiceImpl.REGISTRATION_FAILED_MESSAGE_NOT_READABLE;
import static rocks.metaldetector.service.notification.TelegramConfigServiceImpl.REGISTRATION_SUCCESSFUL_MESSAGE;

@ExtendWith(MockitoExtension.class)
class TelegramConfigServiceImplTest implements WithAssertions {

  @Mock
  private TelegramConfigRepository telegramConfigRepository;

  @Mock
  private TelegramConfigTransformer telegramConfigTransformer;

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private TelegramMessagingService telegramMessagingService;

  @InjectMocks
  private TelegramConfigServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(telegramConfigRepository, telegramConfigTransformer, currentUserSupplier, telegramMessagingService);
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
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("repository is called on fetching the config")
    void test_repository_called() {
      // given
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(currentUserSupplier).get();

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
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
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
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
      doReturn(Optional.of(TelegramConfigEntity.builder().build())).when(telegramConfigRepository).findByUser(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("generateRegistrationId: user's notification config is fetched")
    void test_users_notification_config_fetched_on_id_generation() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var user = UserEntityFactory.createUser("user", "mail@mail.mail");
      doReturn(user).when(currentUserSupplier).get();
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
    @DisplayName("generateRegistrationId: generated id is saved on existing config")
    void test_generated_id_saved_existing_config() {
      // given
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var id = 666_666;
      var telegramConfig = TelegramConfigEntity.builder().build();
      doReturn(id).when(threadLocalRandomMock).nextInt(anyInt(), anyInt());
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
      doReturn(Optional.of(telegramConfig)).when(telegramConfigRepository).findByUser(any());

      // when
      try (MockedStatic<ThreadLocalRandom> mock = mockStatic(ThreadLocalRandom.class)) {
        mock.when(ThreadLocalRandom::current).thenReturn(threadLocalRandomMock);
        underTest.generateRegistrationId();
      }

      // then
      verify(telegramConfigRepository).save(telegramConfig);
    }

    @Test
    @DisplayName("generateRegistrationId: generated id is saved on new config")
    void test_generated_id_saved_new_config() {
      // given
      ArgumentCaptor<TelegramConfigEntity> argumentCaptor = ArgumentCaptor.forClass(TelegramConfigEntity.class);
      var threadLocalRandomMock = mock(ThreadLocalRandom.class);
      var id = 666_666;
      doReturn(id).when(threadLocalRandomMock).nextInt(anyInt(), anyInt());
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
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
      doReturn(UserEntityFactory.createUser("user", "mail@mail.mail")).when(currentUserSupplier).get();
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
}
