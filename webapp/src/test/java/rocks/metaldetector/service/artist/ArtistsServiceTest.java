package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultEntryDtoFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistsServiceTest implements WithAssertions {

  private static final long DISCOGS_ID = 1L;
  private static final String ARTIST_NAME = "A";

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private UserEntity userEntityMock;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private DiscogsService discogsService;

  @Mock
  private ArtistDtoTransformer artistDtoTransformer;

  @InjectMocks
  private ArtistsServiceImpl underTest;

  private ArtistEntity artistEntity;
  private ArtistDto artistDto;

  @AfterEach
  void tearDown() {
    reset(discogsService, artistRepository, currentUserSupplier, artistDtoTransformer);
  }

  @BeforeEach
  void setUp() {
    artistEntity = new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null);
    artistDto = new ArtistDto(DISCOGS_ID, ARTIST_NAME, null);
  }

  @Test
  @DisplayName("findArtistByDiscogsId() should return the correct artist if it exists")
  void find_by_discogs_id_should_return_correct_artist() {
    // given
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(artistEntity));
    when(artistDtoTransformer.transform(any())).thenReturn(ArtistDtoFactory.createDefault());

    // when
    Optional<ArtistDto> artistOptional = underTest.findArtistByDiscogsId(DISCOGS_ID);

    // then
    assertThat(artistOptional).isPresent();
    assertThat(artistOptional.get()).isEqualTo(artistDto);
  }

  @Test
  @DisplayName("findArtistByDiscogsId() should call artist repository")
  void find_by_discogs_id_should_call_artist_repository() {
    // given
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(artistEntity));

    // when
    underTest.findArtistByDiscogsId(DISCOGS_ID);

    // then
    verify(artistRepository, times(1)).findByArtistDiscogsId(DISCOGS_ID);
  }

  @Test
  @DisplayName("findArtistByDiscogsId() should call artist dto transformer")
  void find_by_discogs_id_should_call_artist_dto_transformer() {
    // given
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(artistEntity));

    // when
    underTest.findArtistByDiscogsId(DISCOGS_ID);

    // then
    verify(artistDtoTransformer, times(1)).transform(artistEntity);
  }

  @Test
  @DisplayName("findArtistByDiscogsId() should return an empty optional if artist does not exist")
  void find_by_discogs_id_should_return_empty_optional() {
    // when
    Optional<ArtistDto> artistOptional = underTest.findArtistByDiscogsId(DISCOGS_ID);

    // then
    assertThat(artistOptional).isEmpty();
  }

  @Test
  @DisplayName("findAllByArtistDiscogsIdIn() should return all given entities that exist")
  void find_all_by_discogs_ids_should_return_all_entities_that_exist() {
    // given
    when(artistRepository.findAllByArtistDiscogsIdIn(anyLong(), anyLong())).thenReturn(List.of(artistEntity));
    when(artistDtoTransformer.transform(any())).thenReturn(ArtistDtoFactory.createDefault());

    // when
    List<ArtistDto> artists = underTest.findAllArtistsByDiscogsIds(DISCOGS_ID, 0L);

    // then
    assertThat(artists).hasSize(1);
    assertThat(artists.get(0)).isEqualTo(artistDto);
  }

  @Test
  @DisplayName("findAllByArtistDiscogsIdIn() should call artist repository")
  void find_all_by_discogs_ids_should_call_artist_repository() {
    // when
    underTest.findAllArtistsByDiscogsIds(DISCOGS_ID, 0L);

    // then
    verify(artistRepository, times(1)).findAllByArtistDiscogsIdIn(DISCOGS_ID, 0L);
  }

  @Test
  @DisplayName("findAllByArtistDiscogsIdIn() should call artist dto transformer")
  void find_all_by_discogs_ids_should_call_artist_dto_transformer() {
    // given
    when(artistRepository.findAllByArtistDiscogsIdIn(anyLong(), anyLong())).thenReturn(List.of(artistEntity));

    // when
    underTest.findAllArtistsByDiscogsIds(anyLong(), anyLong());

    // then
    verify(artistDtoTransformer, times(1)).transform(artistEntity);
  }

  @Test
  @DisplayName("existsArtistByDiscogsId() should return true if given entity exists")
  void exists_by_discogs_id_should_return_true() {
    // given
    when(artistRepository.existsByArtistDiscogsId(anyLong())).thenReturn(true);

    // when
    boolean exists = underTest.existsArtistByDiscogsId(DISCOGS_ID);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsArtistByDiscogsId() should return false if given entity does not exist")
  void exists_by_discogs_id_should_return_false() {
    // given
    when(artistRepository.existsByArtistDiscogsId(anyLong())).thenReturn(false);

    // when
    boolean exists = underTest.existsArtistByDiscogsId(DISCOGS_ID);

    // then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("existsArtistByDiscogsId() should call artist repository")
  void exists_by_discogs_id_should_call_artist_repository() {
    // when
    underTest.existsArtistByDiscogsId(DISCOGS_ID);

    // then
    verify(artistRepository, times(1)).existsByArtistDiscogsId(DISCOGS_ID);
  }

  @Test
  @DisplayName("Should pass provided arguments to discogs service")
  void searchDiscogsByName_should_pass_arguments() {
    // given
    var artistQueryString = "the query";
    var pageable = PageRequest.of(1, 10);
    doReturn(Collections.emptySet()).when(userEntityMock).getFollowedArtists();
    doReturn(userEntityMock).when(currentUserSupplier).get();
    doReturn(DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());

    // when
    underTest.searchDiscogsByName(artistQueryString, pageable);

    // then
    verify(discogsService, times(1)).searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
  }

  @Test
  @DisplayName("Should return search results from discogs")
  void searchDiscogsByName_should_return_search_results() {
    // given
    var expectedSearchResults = DiscogsArtistSearchResultDtoFactory.createDefault();
    doReturn(Collections.emptySet()).when(userEntityMock).getFollowedArtists();
    doReturn(userEntityMock).when(currentUserSupplier).get();
    doReturn(expectedSearchResults).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());

    // when
    var searchResults = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

    // then
    assertThat(searchResults).isEqualTo(expectedSearchResults);
  }

  @Test
  @DisplayName("Should mark all already followed artists")
  void searchDiscogsByName_should_mark_already_followed_artists() {
    // given
    var discogsSearchResults = DiscogsArtistSearchResultDtoFactory.createDefault();
    discogsSearchResults.setSearchResults(createListOfSearchResultEntries(1, 2, 3));
    doReturn(createSetOfArtistEntities(1, 3)).when(userEntityMock).getFollowedArtists();
    doReturn(userEntityMock).when(currentUserSupplier).get();
    doReturn(discogsSearchResults).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());

    // when
    var searchResults = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

    // then
    assertThat(searchResults.getSearchResults().get(0).isFollowed()).isTrue();
    assertThat(searchResults.getSearchResults().get(1).isFollowed()).isFalse();
    assertThat(searchResults.getSearchResults().get(2).isFollowed()).isTrue();
  }

  private List<DiscogsArtistSearchResultEntryDto> createListOfSearchResultEntries(long... artistIds) {
    return List.of(
        DiscogsArtistSearchResultEntryDtoFactory.withId(artistIds[0]),
        DiscogsArtistSearchResultEntryDtoFactory.withId(artistIds[1]),
        DiscogsArtistSearchResultEntryDtoFactory.withId(artistIds[2])
    );
  }

  private Set<ArtistEntity> createSetOfArtistEntities(long... artistIds) {
    return Set.of(
        ArtistEntityFactory.withDiscogsId(artistIds[0]),
        ArtistEntityFactory.withDiscogsId(artistIds[1])
    );
  }
}
