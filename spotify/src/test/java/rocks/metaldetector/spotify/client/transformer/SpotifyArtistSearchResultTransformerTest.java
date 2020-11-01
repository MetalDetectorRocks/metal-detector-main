package rocks.metaldetector.spotify.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistDtoFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistSearchResultTransformerTest implements WithAssertions {

  @Mock
  private SpotifyArtistTransformer artistTransformer;

  @Mock
  private SpotifyPaginationTransformer paginationTransformer;

  @InjectMocks
  private SpotifyArtistSearchResultTransformer underTest;

  @AfterEach
  void tearDown() {
    reset(artistTransformer, paginationTransformer);
  }

  @Test
  @DisplayName("Should call paginationTransformer")
  void should_call_pagination_transformer() {
    // given
    SpotifyArtistSearchResultContainer container = SpotifyArtistSearchResultContainerFactory.createDefault();

    // when
    underTest.transform(container);

    // then
    verify(paginationTransformer).transform(container.getArtists());
  }

  @Test
  @DisplayName("Should transform SpotifyArtist with SpotifyArtistTransformer")
  void should_use_artist_transformer() {
    // given
    SpotifyArtistSearchResultContainer container = SpotifyArtistSearchResultContainerFactory.createDefault();

    // when
    underTest.transform(container);

    // then
    for (SpotifyArtist spotifyArtist : container.getArtists().getItems()) {
      verify(artistTransformer).transform(spotifyArtist);
    }
  }

  @Test
  @DisplayName("Should set result from SpotifyArtistTransformer")
  void should_set_result_from_artist_transformer() {
    // given
    SpotifyArtistSearchResultContainer container = SpotifyArtistSearchResultContainerFactory.createDefault();
    for (SpotifyArtist spotifyArtist : container.getArtists().getItems()) {
      var spotifyArtistDtoMock = SpotifyArtistDtoFactory.withArtistName(spotifyArtist.getName());
      doReturn(spotifyArtistDtoMock).when(artistTransformer).transform(spotifyArtist);
    }

    // when
    SpotifyArtistSearchResultDto result = underTest.transform(container);

    // then
    for (int index = 0; index < container.getArtists().getItems().size(); index++) {
      var givenArtist = container.getArtists().getItems().get(index);
      var resultArtist = result.getSearchResults().get(index);
      assertThat(resultArtist.getName()).isEqualTo(givenArtist.getName());
    }
  }
}