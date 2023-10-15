package rocks.metaldetector.service.notification.messaging;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.service.telegram.TelegramService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.notification.messaging.TelegramNotificationSender.TODAYS_ANNOUNCEMENTS_TEXT;
import static rocks.metaldetector.service.notification.messaging.TelegramNotificationSender.TODAYS_RELEASES_TEXT;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationSenderTest implements WithAssertions {

  private static final AbstractUserEntity USER = UserEntityFactory.createUser("user", "user@user.user");

  @Mock
  private TelegramService telegramService;

  @Mock
  private TelegramNotificationFormatter telegramNotificationFormatter;

  @InjectMocks
  private TelegramNotificationSender underTest;

  @AfterEach
  void tearDown() {
    reset(telegramService, telegramNotificationFormatter);
  }

  @Nested
  @DisplayName("Frequency message tests")
  class FrequencyTests {

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
    @DisplayName("telegramService is called with user")
    void test_telegram_service_called_with_user() {
      // given
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatFrequencyNotificationMessage(anyList(), anyList());

      // when
      underTest.sendFrequencyMessage(USER, Collections.emptyList(), Collections.emptyList());

      // then
      verify(telegramService).sendMessage(eq(USER), any());
    }

    @Test
    @DisplayName("telegramService is called with formatted message")
    void test_telegram_service_called_with_message() {
      // given
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatFrequencyNotificationMessage(anyList(), anyList());

      // when
      underTest.sendFrequencyMessage(USER, Collections.emptyList(), Collections.emptyList());

      // then
      verify(telegramService).sendMessage(any(), eq(message));
    }
  }

  @Nested
  @DisplayName("Release date message tests")
  class ReleaseDateTests {

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
    @DisplayName("telegramService is called with user")
    void test_telegram_service_called_with_user() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatDateNotificationMessage(anyList(), any());

      // when
      underTest.sendReleaseDateMessage(USER, releases);

      // then
      verify(telegramService).sendMessage(eq(USER), any());
    }

    @Test
    @DisplayName("telegramService is called with formatted message")
    void test_telegram_service_called_with_message() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatDateNotificationMessage(anyList(), any());

      // when
      underTest.sendReleaseDateMessage(USER, releases);

      // then
      verify(telegramService).sendMessage(any(), eq(message));
    }
  }

  @Nested
  @DisplayName("Announcement date message tests")
  class AnnouncementDateTests {

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
    @DisplayName("telegramService is called with user")
    void test_telegram_service_called_with_user() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatDateNotificationMessage(anyList(), any());

      // when
      underTest.sendAnnouncementDateMessage(USER, releases);

      // then
      verify(telegramService).sendMessage(eq(USER), any());
    }

    @Test
    @DisplayName("telegramService is called with formatted message")
    void test_telegram_service_called_with_message() {
      // given
      var releases = List.of(ReleaseDtoFactory.createDefault());
      var message = "message";
      doReturn(message).when(telegramNotificationFormatter).formatDateNotificationMessage(anyList(), any());

      // when
      underTest.sendAnnouncementDateMessage(USER, releases);

      // then
      verify(telegramService).sendMessage(any(), eq(message));
    }
  }
}
