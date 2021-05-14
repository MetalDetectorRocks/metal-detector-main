package rocks.metaldetector.service.notification.messaging;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.testutil.DtoFactory;

import java.util.List;

class TelegramNotificationFormatterTest implements WithAssertions {

  private final TelegramNotificationFormatter underTest = new TelegramNotificationFormatter();

  @Test
  @DisplayName("message for frequency notification is formatted")
  void test_frequency_message_formatted() {
    // given
    var upcomingRelease = DtoFactory.ReleaseDtoFactory.withArtistName("A");
    var recentRelease = DtoFactory.ReleaseDtoFactory.withArtistName("B");
    var expectedMessage = "Your new metal releases!\n"
                          + "The following releases have been released recently or will be released in the next days:\n\n"
                          + "  - " + recentRelease.getArtist() + " - " + recentRelease.getAlbumTitle() + " - " + recentRelease.getReleaseDateAsDisplayString() + "\n\n"
                          + "  - " + upcomingRelease.getArtist() + " - " + upcomingRelease.getAlbumTitle() + " - " + upcomingRelease.getReleaseDateAsDisplayString() + "\n";

    // when
    var result = underTest.formatFrequencyNotificationMessage(List.of(upcomingRelease), List.of(recentRelease));

    // then
    assertThat(result).isEqualTo(expectedMessage);
  }

  @Test
  @DisplayName("message for specific date notification is formatted")
  void test_date_message_formatted() {
    // given
    var release = DtoFactory.ReleaseDtoFactory.withArtistName("A");
    var message = "Today's metal releases:";
    var expectedMessage = message + "\n"
                          + "  - " + release.getArtist() + " - " + release.getAlbumTitle() + " - " + release.getReleaseDateAsDisplayString() + "\n";

    // when
    var result = underTest.formatDateNotificationMessage(List.of(release), message);

    // then
    assertThat(result).isEqualTo(expectedMessage);
  }
}
