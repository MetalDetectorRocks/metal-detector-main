package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultDtoFactory;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.api.response.ArtistSearchResponseEntryDto;
import rocks.metaldetector.web.transformer.ArtistSearchResponseTransformer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.testutil.DtoFactory.ArtistSearchResponseEntryDtoFactory;
import static rocks.metaldetector.testutil.DtoFactory.ArtistSearchResponseFactory;
import static rocks.metaldetector.testutil.DtoFactory.SpotifyArtistSearchResultDtoFactory;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest implements WithAssertions {

  private static final String EXTERNAL_ID = "1";
  private static final String ARTIST_NAME = "A";
  private static final ArtistSource ARTIST_SOURCE = DISCOGS;

  @Mock
  private CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private DiscogsService discogsService;

  @Mock
  private SpotifyService spotifyService;

  @Mock
  private ArtistTransformer artistTransformer;

  @Mock
  private ArtistSearchResponseTransformer searchResponseTransformer;

  @Mock
  private UserEntity userEntityMock;

  @InjectMocks
  private ArtistServiceImpl underTest;

  private ArtistEntity artistEntity;
  private ArtistDto artistDto;

  @AfterEach
  void tearDown() {
    reset(currentPublicUserIdSupplier, artistRepository, userRepository, discogsService, artistTransformer, spotifyService, searchResponseTransformer);
  }

  @BeforeEach
  void setUp() {
    artistEntity = new ArtistEntity(EXTERNAL_ID, ARTIST_NAME, null, DISCOGS);
    artistDto = new ArtistDto(EXTERNAL_ID, ARTIST_NAME, null, "Discogs", null);
  }

  @Test
  @DisplayName("findArtistByExternalId() should return the correct artist if it exists")
  void find_by_discogs_id_should_return_correct_artist() {
    // given
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(artistEntity));
    when(artistTransformer.transform(any(ArtistEntity.class))).thenReturn(ArtistDtoFactory.createDefault());

    // when
    Optional<ArtistDto> artistOptional = underTest.findArtistByExternalId(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    assertThat(artistOptional).isPresent();
    assertThat(artistOptional.get()).isEqualTo(artistDto);
  }

  @Test
  @DisplayName("findArtistByExternalId() should call artist repository")
  void find_by_discogs_id_should_call_artist_repository() {
    // given
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(artistEntity));

    // when
    underTest.findArtistByExternalId(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(artistRepository, times(1)).findByExternalIdAndSource(EXTERNAL_ID, ARTIST_SOURCE);
  }

  @Test
  @DisplayName("findArtistByExternalId() should call artist dto transformer")
  void find_by_discogs_id_should_call_artist_dto_transformer() {
    // given
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(artistEntity));

    // when
    underTest.findArtistByExternalId(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(artistTransformer, times(1)).transform(artistEntity);
  }

  @Test
  @DisplayName("findArtistByExternalId() should return an empty optional if artist does not exist")
  void find_by_discogs_id_should_return_empty_optional() {
    // when
    Optional<ArtistDto> artistOptional = underTest.findArtistByExternalId(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    assertThat(artistOptional).isEmpty();
  }

  @Test
  @DisplayName("findAllByArtistExternalIdIn() should return all given entities that exist")
  void find_all_by_discogs_ids_should_return_all_entities_that_exist() {
    // given
    when(artistRepository.findAllByExternalIdIn(any())).thenReturn(List.of(artistEntity));
    when(artistTransformer.transform(any(ArtistEntity.class))).thenReturn(ArtistDtoFactory.createDefault());

    // when
    List<ArtistDto> artists = underTest.findAllArtistsByExternalIds(List.of(EXTERNAL_ID, "0"));

    // then
    assertThat(artists).hasSize(1);
    assertThat(artists.get(0)).isEqualTo(artistDto);
  }

  @Test
  @DisplayName("findAllByArtistExternalIdIn() should call artist repository")
  void find_all_by_discogs_ids_should_call_artist_repository() {
    // when
    underTest.findAllArtistsByExternalIds(List.of(EXTERNAL_ID, "0"));

    // then
    verify(artistRepository, times(1)).findAllByExternalIdIn(List.of(EXTERNAL_ID, "0"));
  }

  @Test
  @DisplayName("findAllByArtistExternalIdIn() should call artist dto transformer")
  void find_all_by_discogs_ids_should_call_artist_dto_transformer() {
    // given
    when(artistRepository.findAllByExternalIdIn(any())).thenReturn(List.of(artistEntity));

    // when
    underTest.findAllArtistsByExternalIds(List.of(EXTERNAL_ID, "0"));

    // then
    verify(artistTransformer, times(1)).transform(artistEntity);
  }

  @Test
  @DisplayName("existsArtistByExternalId() should return true if given entity exists")
  void exists_by_discogs_id_should_return_true() {
    // given
    when(artistRepository.existsByExternalIdAndSource(any(), any())).thenReturn(true);

    // when
    boolean exists = underTest.existsArtistByExternalId(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsArtistByExternalId() should return false if given entity does not exist")
  void exists_by_discogs_id_should_return_false() {
    // given
    when(artistRepository.existsByExternalIdAndSource(any(), any())).thenReturn(false);

    // when
    boolean exists = underTest.existsArtistByExternalId(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("existsArtistByExternalId() should call artist repository")
  void exists_by_discogs_id_should_call_artist_repository() {
    // when
    underTest.existsArtistByExternalId(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(artistRepository, times(1)).existsByExternalIdAndSource(EXTERNAL_ID, ARTIST_SOURCE);
  }

}
