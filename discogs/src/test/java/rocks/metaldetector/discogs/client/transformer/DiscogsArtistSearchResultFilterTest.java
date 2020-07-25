package rocks.metaldetector.discogs.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory;

import java.util.List;

class DiscogsArtistSearchResultFilterTest implements WithAssertions {

  private DiscogsArtistSearchResultFilter underTest = new DiscogsArtistSearchResultFilter();

  @Test
  @DisplayName("Should filter results that don't match lowercase query")
  void should_filter() {
    // given
    DiscogsArtistSearchResultContainer container = DiscogsDtoFactory.DiscogsArtistSearchResultContainerFactory.withArtistNames(List.of("ab", "AB", "Ab", "AA", "bb"));

    // when
    DiscogsArtistSearchResultContainer result = underTest.filterDiscogsSearchResults(container, "aB");

    // then
    assertThat(result.getResults().size()).isEqualTo(3);
  }
}