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
class NotificationSenderSupplierTest implements WithAssertions {

  @Mock
  private NotificationSender emailService;

  @Mock
  private NotificationSender telegramService;

  private NotificationSenderSupplier underTest;

  @BeforeEach
  void setup() {
    underTest = new NotificationSenderSupplier(emailService, telegramService);
  }

  @AfterEach
  void tearDown() {
    reset(emailService, telegramService);
  }

  @Test
  @DisplayName("emailService is returned")
  void test_email_service_returned() {
    // when
    var result = underTest.apply(EMAIL);

    // then
    assertThat(result).isEqualTo(emailService);
  }

  @Test
  @DisplayName("telegramService is returned")
  void test_telegram_service_returned() {
    // when
    var result = underTest.apply(TELEGRAM);

    // then
    assertThat(result).isEqualTo(telegramService);
  }
}
