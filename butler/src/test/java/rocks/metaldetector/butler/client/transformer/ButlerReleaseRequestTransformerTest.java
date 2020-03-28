package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;

import java.time.LocalDate;
import java.util.List;

class ButlerReleaseRequestTransformerTest implements WithAssertions {

  private ButlerReleaseRequestTransformer underTest = new ButlerReleaseRequestTransformer();

  @Test
  @DisplayName("Should transform arguments to ButlerReleasesRequest")
  void should_transform() {
    // given
    Iterable<String> artists = List.of("A", "B", "C");
    LocalDate from = LocalDate.of(2020, 1, 1);
    LocalDate to = LocalDate.of(2020, 12, 1);

    // when
    ButlerReleasesRequest result = underTest.transform(artists, from, to);

    // then
    assertThat(result.getArtists()).isEqualTo(artists);
    assertThat(result.getDateFrom()).isEqualTo(from);
    assertThat(result.getDateTo()).isEqualTo(to);
  }
}
