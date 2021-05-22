package rocks.metaldetector.service.notification.messaging;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigEntity;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.telegram.facade.TelegramMessagingService;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.service.notification.messaging.TelegramNotificationSender.TODAYS_ANNOUNCEMENTS_TEXT;
import static rocks.metaldetector.service.notification.messaging.TelegramNotificationSender.TODAYS_RELEASES_TEXT;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationSenderTest implements WithAssertions {

  private static final AbstractUserEntity USER = UserEntityFactory.createUser("user", "user@user.user");

  @Mock
  private TelegramMessagingService telegramMessagingService;

  @Mock
  private TelegramConfigRepository telegramConfigRepository;

  @Mock
  private TelegramNotificationFormatter telegramNotificationFormatter;

  @InjectMocks
  private TelegramNotificationSender underTest;

  private TelegramConfigEntity telegramConfig;

  @BeforeEach
  void setup() {
    telegramConfig = TelegramConfigEntity.builder().chatId(666).build();
    doReturn(Optional.of(telegramConfig)).when(telegramConfigRepository).findByUser(any());
  }

  @AfterEach
  void tearDown() {
    reset(telegramConfigRepository, telegramMessagingService, telegramNotificationFormatter);
  }

  @Nested
  @DisplayName("Frequency message tests")
  class FrequencyTests {

    @Test
    @DisplayName("repository is called")
    void test_repository_called() {
      // when
      underTest.sendFrequencyMessage(USER, Collections.emptyList(), Collections.emptyList());

      // then
      verify(telegramConfigRepository).findByUser(USER);
    }

    @Test
    @DisplayName("exception is thrown if no config could not be found")
    void test_exception_thrown() {
      // given
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());

      // when
      var throwable = catchThrowable(() -> underTest.sendFrequencyMessage(USER, Collections.emptyList(), Collections.emptyList()));

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("notificationFormatter is called if chat id is set")
    void test_notification_formatter_called() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());

      // when
      underTest.sendFrequencyMessage(USER, releases, releases);

      // then
      verify(telegramNotificationFormatter).formatFrequencyNotificationMessage(releases, releases);
    }

    @Test
    @DisplayName("notificationFormatter is not called if chat id is null")
    void test_notification_formatter_not_called() {
      // given
      telegramConfig.setChatId(null);

      // when
      underTest.sendFrequencyMessage(USER, Collections.emptyList(), Collections.emptyList());

      // then
      verifyNoInteractions(telegramNotificationFormatter);
    }

    @Test
    @DisplayName("messagingService is called with formatted message")
    void test_messaging_service_called() {
      // given
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatFrequencyNotificationMessage(anyList(), anyList());

      // when
      underTest.sendFrequencyMessage(USER, Collections.emptyList(), Collections.emptyList());

      // then
      verify(telegramMessagingService).sendMessage(telegramConfig.getChatId(), message);
    }
  }

  @Nested
  @DisplayName("Release date message tests")
  class ReleaseDateTests {

    @Test
    @DisplayName("repository is called")
    void test_repository_called() {
      // when
      underTest.sendReleaseDateMessage(USER, List.of(ReleaseDtoFactory.createDefault()));

      // then
      verify(telegramConfigRepository).findByUser(USER);
    }

    @Test
    @DisplayName("exception is thrown if no config could not be found")
    void test_exception_thrown() {
      // given
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());

      // when
      var throwable = catchThrowable(() -> underTest.sendReleaseDateMessage(USER, Collections.emptyList()));

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("notificationFormatter is called if chat id is set")
    void test_notification_formatter_called() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());

      // when
      underTest.sendReleaseDateMessage(USER, releases);

      // then
      verify(telegramNotificationFormatter).formatDateNotificationMessage(releases, TODAYS_RELEASES_TEXT);
    }

    @Test
    @DisplayName("notificationFormatter is not called if chat id is null")
    void test_notification_formatter_not_called() {
      // given
      telegramConfig.setChatId(null);

      // when
      underTest.sendReleaseDateMessage(USER, Collections.emptyList());

      // then
      verifyNoInteractions(telegramNotificationFormatter);
    }

    @Test
    @DisplayName("messagingService is called with formatted message")
    void test_messaging_service_called() {
      // given
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatDateNotificationMessage(anyList(), anyString());

      // when
      underTest.sendReleaseDateMessage(USER, Collections.emptyList());

      // then
      verify(telegramMessagingService).sendMessage(telegramConfig.getChatId(), message);
    }
  }

  @Nested
  @DisplayName("Announcement date message tests")
  class AnnouncementDateTests {

    @Test
    @DisplayName("repository is called")
    void test_repository_called() {
      // when
      underTest.sendAnnouncementDateMessage(USER, List.of(ReleaseDtoFactory.createDefault()));

      // then
      verify(telegramConfigRepository).findByUser(USER);
    }

    @Test
    @DisplayName("exception is thrown if no config could not be found")
    void test_exception_thrown() {
      // given
      doReturn(Optional.empty()).when(telegramConfigRepository).findByUser(any());

      // when
      var throwable = catchThrowable(() -> underTest.sendAnnouncementDateMessage(USER, Collections.emptyList()));

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("notificationFormatter is called if chat id is set")
    void test_notification_formatter_called() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());

      // when
      underTest.sendAnnouncementDateMessage(USER, releases);

      // then
      verify(telegramNotificationFormatter).formatDateNotificationMessage(releases, TODAYS_ANNOUNCEMENTS_TEXT);
    }

    @Test
    @DisplayName("notificationFormatter is not called if chat id is null")
    void test_notification_formatter_not_called() {
      // given
      telegramConfig.setChatId(null);

      // when
      underTest.sendAnnouncementDateMessage(USER, Collections.emptyList());

      // then
      verifyNoInteractions(telegramNotificationFormatter);
    }

    @Test
    @DisplayName("messagingService is called with formatted message")
    void test_messaging_service_called() {
      // given
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatDateNotificationMessage(anyList(), anyString());

      // when
      underTest.sendAnnouncementDateMessage(USER, Collections.emptyList());

      // then
      verify(telegramMessagingService).sendMessage(telegramConfig.getChatId(), message);
    }
  }
}
