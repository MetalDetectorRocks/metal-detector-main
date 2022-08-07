package rocks.metaldetector.service.notification.messaging;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.reset;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;

@ExtendWith(MockitoExtension.class)
class NotificationSenderFunctionTest implements WithAssertions {

  @Mock
  private NotificationSender emailNotificationSender;

  @Mock
  private NotificationSender telegramNotificationSender;

  private NotificationSenderFunction underTest;

  @BeforeEach
  void setup() {
    underTest = new NotificationSenderFunction(emailNotificationSender, telegramNotificationSender);
  }

  @AfterEach
  void tearDown() {
    reset(emailNotificationSender, telegramNotificationSender);
  }

  @Test
  @DisplayName("emailService is returned")
  void test_email_service_returned() {
    // when
    var result = underTest.apply(EMAIL);

    // then
    assertThat(result).isEqualTo(emailNotificationSender);
  }

  @Test
  @DisplayName("telegramService is returned")
  void test_telegram_service_returned() {
    // when
    var result = underTest.apply(TELEGRAM);

    // then
    assertThat(result).isEqualTo(telegramNotificationSender);
  }
}
