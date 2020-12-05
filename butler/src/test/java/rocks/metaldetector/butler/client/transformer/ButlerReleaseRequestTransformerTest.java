package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;

import java.time.LocalDate;
import java.util.List;

class ButlerReleaseRequestTransformerTest implements WithAssertions {

  private final ButlerReleaseRequestTransformer underTest = new ButlerReleaseRequestTransformer();

  @Test
  @DisplayName("Should transform arguments to ButlerReleasesRequest")
  void should_transform() {
    // given
    Iterable<String> artists = List.of("A", "B", "C");
    TimeRange timeRange = new TimeRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 1));
    PageRequest pageRequest = new PageRequest(1, 10, null);

    // when
    ButlerReleasesRequest result = underTest.transform(artists, timeRange, pageRequest);

    // then
    assertThat(result.getArtists()).isEqualTo(artists);
    assertThat(result.getDateFrom()).isEqualTo(timeRange.getDateFrom());
    assertThat(result.getDateTo()).isEqualTo(timeRange.getDateTo());
    assertThat(result.getPage()).isEqualTo(pageRequest.getPage());
    assertThat(result.getSize()).isEqualTo(pageRequest.getSize());
  }

  @Test
  @DisplayName("page and size is 0 if PageRequest is null")
  void page_request_can_be_null() {
    // given
    Iterable<String> artists = List.of("A", "B", "C");
    TimeRange timeRange = new TimeRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 1));

    // when
    ButlerReleasesRequest result = underTest.transform(artists, timeRange, null);

    // then

    assertThat(result.getPage()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(0);
  }
}
