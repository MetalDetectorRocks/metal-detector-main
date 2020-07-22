package rocks.metaldetector.discogs.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;

import java.util.stream.Stream;

import static rocks.metaldetector.discogs.client.DiscogsArtistSearchRestClientMock.METALLICA_ID;
import static rocks.metaldetector.discogs.client.DiscogsArtistSearchRestClientMock.SLAYER_ID;

class DiscogsArtistSearchRestClientMockTest implements WithAssertions {

  private DiscogsArtistSearchRestClientMock underTest = new DiscogsArtistSearchRestClientMock();

  @Test
  @DisplayName("Should return results when searching for a name")
  void should_return_search_results() {
    // when
    DiscogsArtistSearchResultContainer result = underTest.searchByName("", 0, 0);

    // then
    assertThat(result.getResults()).hasSize(2);
  }

  @ParameterizedTest(name = "Should return an artist when searching by id {0}")
  @MethodSource("idProvider")
  @DisplayName("Should return an artist when searching by an known id")
  void should_return_artist(String externalId) {
    // when
    DiscogsArtist result = underTest.searchById(externalId);

    // then
    assertThat(result.getId()).isEqualTo(Long.parseLong(externalId));
  }

  private static Stream<Arguments> idProvider() {
    return Stream.of(
            Arguments.of(METALLICA_ID),
            Arguments.of(SLAYER_ID)
    );
  }
}
