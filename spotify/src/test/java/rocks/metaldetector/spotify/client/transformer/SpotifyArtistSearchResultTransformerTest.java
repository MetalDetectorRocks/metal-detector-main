package rocks.metaldetector.spotify.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.spotify.api.search.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultEntryDto;
import rocks.metaldetector.support.Pagination;

import java.util.stream.Stream;

import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory.withIndivualPagination;

class SpotifyArtistSearchResultTransformerTest implements WithAssertions {

  private final SpotifyArtistSearchResultTransformer underTest = new SpotifyArtistSearchResultTransformer();

  @ParameterizedTest
  @MethodSource("paginationProvider")
  @DisplayName("Should transform Spotify parameters to Pagination")
  void should_transform_pagination(SpotifyArtistSearchResultContainer resultContainer, Pagination expectedPagination) {
    // when
    SpotifyArtistSearchResultDto result = underTest.transform(resultContainer);

    // then
    assertThat(result.getPagination()).isEqualTo(expectedPagination);
  }

  @Test
  @DisplayName("Should transform SpotifyArtistSearchResult to SpotifyArtistSearchResultEntryDto")
  void should_transform() {
    // given
    SpotifyArtistSearchResultContainer container = SpotifyArtistSearchResultContainerFactory.createDefault();

    // when
    SpotifyArtistSearchResultDto result = underTest.transform(container);

    // then
    assertThat(result.getSearchResults().size()).isEqualTo(container.getArtists().getItems().size());
    for (int index = 0; index < result.getSearchResults().size(); index++) {
      SpotifyArtist givenEntry = container.getArtists().getItems().get(index);
      SpotifyArtistSearchResultEntryDto resultEntry = result.getSearchResults().get(index);
      assertThat(resultEntry).isEqualTo(
          SpotifyArtistSearchResultEntryDto.builder()
              .id(givenEntry.getId())
              .name(givenEntry.getName())
              .imageUrl(givenEntry.getImages().get(0).getUrl())
              .uri(givenEntry.getUri())
              .followed(false)
              .genres(givenEntry.getGenres())
              .popularity(givenEntry.getPopularity())
              .build()
      );
    }
  }

  private static Stream<Arguments> paginationProvider() {
    return Stream.of(
        Arguments.of(withIndivualPagination(0, 10, 1), new Pagination(1, 1, 10)),
        Arguments.of(withIndivualPagination(10, 10, 15), new Pagination(2, 2, 10)),
        Arguments.of(withIndivualPagination(5, 20, 30), new Pagination(2, 1, 20))
    );
  }
}