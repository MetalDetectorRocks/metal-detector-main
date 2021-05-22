package rocks.metaldetector.persistence.domain.notification;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;

class NotificationChannelTest implements WithAssertions {

  @Test
  @DisplayName("email channel is returned")
  void test_email_channel_is_returned() {
    // when
    var result = NotificationChannel.from("EMAIL");

    // then
    assertThat(result).isEqualTo(EMAIL);
  }

  @Test
  @DisplayName("telegram channel is returned")
  void test_telegram_channel_is_returned() {
    // when
    var result = NotificationChannel.from("TELEGRAM");

    // then
    assertThat(result).isEqualTo(TELEGRAM);
  }

  @Test
  @DisplayName("exception is thrown if channel does not exist")
  void test_exception_thrown() {
    // given
    var unknownChannel = "unkown";

    // when
    var throwable = catchThrowable(() -> NotificationChannel.from(unknownChannel));

    // then
    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    assertThat(throwable).hasMessageContaining(unknownChannel);
  }
}
