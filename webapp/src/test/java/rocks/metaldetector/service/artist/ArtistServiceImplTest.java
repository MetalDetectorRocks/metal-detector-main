package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.artist.transformer.ArtistDtoTransformer;
import rocks.metaldetector.service.artist.transformer.ArtistEntityTransformer;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.SpotifyArtistDtoFactory;
import rocks.metaldetector.web.transformer.ArtistSearchResponseTransformer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest implements WithAssertions {

  private static final String EXTERNAL_ID = "A";
  private static final String ARTIST_NAME = "A";
  private static final ArtistSource ARTIST_SOURCE = DISCOGS;

  @Mock
  private ArtistDtoTransformer artistDtoTransformer;

  @Mock
  private ArtistEntityTransformer artistEntityTransformer;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private DiscogsService discogsService;

  @Mock
  private ArtistSearchResponseTransformer searchResponseTransformer;

  @Mock
  private SpotifyService spotifyService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private ArtistServiceImpl underTest;

  private final ArtistEntity artistEntity = ArtistEntity.builder().externalId(EXTERNAL_ID).artistName(ARTIST_NAME).source(DISCOGS).build();
  private final ArtistDto artistDto = new ArtistDto(EXTERNAL_ID, ARTIST_NAME, "Discogs", null, 666, Collections.emptyMap());

  @AfterEach
  void tearDown() {
    reset(artistDtoTransformer, artistEntityTransformer, artistRepository, discogsService,
            searchResponseTransformer, spotifyService, userRepository);
  }

  @Test
  @DisplayName("findArtistByExternalId() should return the correct artist if it exists")
  void find_by_discogs_id_should_return_correct_artist() {
    // given
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(artistEntity));
    when(artistDtoTransformer.transformArtistEntity(any(ArtistEntity.class))).thenReturn(ArtistDtoFactory.createDefault());

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
    verify(artistRepository).findByExternalIdAndSource(EXTERNAL_ID, ARTIST_SOURCE);
  }

  @Test
  @DisplayName("findArtistByExternalId() should call artist dto transformer")
  void find_by_discogs_id_should_call_artist_dto_transformer() {
    // given
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(artistEntity));

    // when
    underTest.findArtistByExternalId(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(artistDtoTransformer).transformArtistEntity(artistEntity);
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
    when(artistDtoTransformer.transformArtistEntity(any(ArtistEntity.class))).thenReturn(ArtistDtoFactory.createDefault());

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
    verify(artistRepository).findAllByExternalIdIn(List.of(EXTERNAL_ID, "0"));
  }

  @Test
  @DisplayName("findAllByArtistExternalIdIn() should call artist dto transformer")
  void find_all_by_discogs_ids_should_call_artist_dto_transformer() {
    // given
    when(artistRepository.findAllByExternalIdIn(any())).thenReturn(List.of(artistEntity));

    // when
    underTest.findAllArtistsByExternalIds(List.of(EXTERNAL_ID, "0"));

    // then
    verify(artistDtoTransformer).transformArtistEntity(artistEntity);
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
    verify(artistRepository).existsByExternalIdAndSource(EXTERNAL_ID, ARTIST_SOURCE);
  }

  @Test
  @DisplayName("persistArtists: should transform each spotify artist with ArtistEntityTransformer")
  void should_transform_spotify_artists_with_artist_entity_transformer() {
    // given
    var spotifyDtos = List.of(SpotifyArtistDtoFactory.withArtistName("a"), SpotifyArtistDtoFactory.withArtistName("b"));

    // when
    underTest.persistSpotifyArtists(spotifyDtos);

    // then
    verify(artistEntityTransformer).transformSpotifyArtistDto(spotifyDtos.get(0));
    verify(artistEntityTransformer).transformSpotifyArtistDto(spotifyDtos.get(1));
  }

  @Test
  @DisplayName("persistArtists: calls artistRepository with all entities")
  void test_artist_repository_called_with_all_entities() {
    // given
    var spotifyDtos = List.of(SpotifyArtistDtoFactory.withArtistName("a"), SpotifyArtistDtoFactory.withArtistName("b"));
    var artistEntities = List.of(ArtistEntityFactory.withExternalId("a"), ArtistEntityFactory.withExternalId("b"));
    doReturn(artistEntities.get(0)).when(artistEntityTransformer).transformSpotifyArtistDto(spotifyDtos.get(0));
    doReturn(artistEntities.get(1)).when(artistEntityTransformer).transformSpotifyArtistDto(spotifyDtos.get(1));

    // when
    underTest.persistSpotifyArtists(spotifyDtos);

    // then
    verify(artistRepository).saveAll(artistEntities);
  }

  @Test
  @DisplayName("findNewArtistIds: artistRepository is called to get all existing artists from given ids")
  void test_artist_repository_called_for_existing_artists() {
    // given
    var artistIds = List.of("id1", "id2");

    // when
    underTest.findNewArtistIds(artistIds);

    // then
    verify(artistRepository).findAllByExternalIdIn(artistIds);
  }

  @Test
  @DisplayName("findNewArtistIds: new ids are returned")
  void test_new_artist_ids_returned() {
    // given
    var existingId = "existingId";
    var newId = "newId";
    var artistIds = List.of(existingId, newId);
    doReturn(List.of(ArtistEntityFactory.withExternalId(existingId))).when(artistRepository).findAllByExternalIdIn(any());

    // when
    var result = underTest.findNewArtistIds(artistIds);

    // then
    assertThat(result).isEqualTo(List.of(newId));
  }
}
