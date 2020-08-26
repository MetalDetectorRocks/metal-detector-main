package rocks.metaldetector.service.summary;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.service.artist.ArtistEntityFactory;
import rocks.metaldetector.service.artist.ArtistTransformer;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.summary.SummaryServiceImpl.RESULT_LIMIT;

@ExtendWith(MockitoExtension.class)
class ArtistCollectorTest implements WithAssertions {

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private ArtistTransformer artistTransformer;

  @InjectMocks
  private ArtistCollector underTest;

  @AfterEach
  void tearDown() {
    reset(artistRepository, artistTransformer);
  }

  @Test
  @DisplayName("artistRepository is called to get top artists")
  void test_artist_repository_is_called_for_top_artists() {
    // when
    underTest.collectTopFollowedArtists();

    // then
    verify(artistRepository, times(1)).findTopArtists(RESULT_LIMIT);
  }

  @Test
  @DisplayName("artistTransformer is called for each artist")
  void test_artist_transformer_is_called_for_artists() {
    // given
    var artistEntities = List.of(ArtistEntityFactory.withExternalId("1"), ArtistEntityFactory.withExternalId("2"));
    doReturn(artistEntities).when(artistRepository).findTopArtists(anyInt());

    // when
    underTest.collectTopFollowedArtists();

    // then
    verify(artistTransformer, times(1)).transform(artistEntities.get(0));
    verify(artistTransformer, times(1)).transform(artistEntities.get(1));
  }

  @Test
  @DisplayName("artist dtos are returned")
  void test_artist_dtos_are_returned() {
    // given
    var artistEntities = List.of(ArtistEntityFactory.withExternalId("1"), ArtistEntityFactory.withExternalId("2"));
    doReturn(artistEntities).when(artistRepository).findTopArtists(anyInt());
    var expectedArtistDtos = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    doReturn(expectedArtistDtos.get(0)).when(artistTransformer).transform(artistEntities.get(0));
    doReturn(expectedArtistDtos.get(1)).when(artistTransformer).transform(artistEntities.get(1));

    // when
    var result = underTest.collectTopFollowedArtists();

    // then
    assertThat(result).isEqualTo(expectedArtistDtos);
  }
}
