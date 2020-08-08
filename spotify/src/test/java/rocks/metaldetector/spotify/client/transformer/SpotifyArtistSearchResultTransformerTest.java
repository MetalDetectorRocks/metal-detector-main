package rocks.metaldetector.spotify.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.spotify.api.search.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.support.Pagination;

import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistDtoFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory.withIndivualPagination;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistSearchResultTransformerTest implements WithAssertions {

  @Mock
  private SpotifyArtistTransformer artistTransformer;

  @InjectMocks
  private SpotifyArtistSearchResultTransformer underTest;

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
  @DisplayName("Should transform SpotifyArtist with SpotifyArtistTransformer")
  void should_use_artist_transformer() {
    // given
    SpotifyArtistSearchResultContainer container = SpotifyArtistSearchResultContainerFactory.createDefault();

    // when
    underTest.transform(container);

    // then
    for (SpotifyArtist spotifyArtist : container.getSearchResult().getArtists()) {
      verify(artistTransformer, times(1)).transform(spotifyArtist);
    }
  }

  @Test
  @DisplayName("Should set result from SpotifyArtistTransformer")
  void should_set_result_from_artist_transformer() {
    // given
    SpotifyArtistSearchResultContainer container = SpotifyArtistSearchResultContainerFactory.createDefault();
    for (SpotifyArtist spotifyArtist : container.getSearchResult().getArtists()) {
      var spotifyArtistDtoMock = SpotifyArtistDtoFactory.withArtistName(spotifyArtist.getName());
      doReturn(spotifyArtistDtoMock).when(artistTransformer).transform(spotifyArtist);
    }

    // when
    SpotifyArtistSearchResultDto result = underTest.transform(container);

    // then
    for (int index = 0; index < container.getSearchResult().getArtists().size(); index++) {
      var givenArtist = container.getSearchResult().getArtists().get(index);
      var resultArtist = result.getSearchResults().get(index);
      assertThat(resultArtist.getName()).isEqualTo(givenArtist.getName());
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