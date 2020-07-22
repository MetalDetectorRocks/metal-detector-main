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
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultEntryDtoFactory;

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

@ExtendWith(MockitoExtension.class)
class ArtistsServiceImplTest implements WithAssertions {

  private static final String EXTERNAL_ID = "1";
  private static final String ARTIST_NAME = "A";

  @Mock
  private CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private DiscogsService discogsService;

  @Mock
  private ArtistTransformer artistTransformer;

  @Mock
  private UserEntity userEntityMock;

  @InjectMocks
  private ArtistsServiceImpl underTest;

  private ArtistEntity artistEntity;
  private ArtistDto artistDto;

  @AfterEach
  void tearDown() {
    reset(currentPublicUserIdSupplier, artistRepository, userRepository, discogsService, artistTransformer);
  }

  @BeforeEach
  void setUp() {
    artistEntity = new ArtistEntity(EXTERNAL_ID, ARTIST_NAME, null, DISCOGS);
    artistDto = new ArtistDto(EXTERNAL_ID, ARTIST_NAME, null, "Discogs");
  }

  @Test
  @DisplayName("findArtistByExternalId() should return the correct artist if it exists")
  void find_by_discogs_id_should_return_correct_artist() {
    // given
    when(artistRepository.findByExternalId(anyString())).thenReturn(Optional.of(artistEntity));
    when(artistTransformer.transform(any())).thenReturn(ArtistDtoFactory.createDefault());

    // when
    Optional<ArtistDto> artistOptional = underTest.findArtistByExternalId(EXTERNAL_ID);

    // then
    assertThat(artistOptional).isPresent();
    assertThat(artistOptional.get()).isEqualTo(artistDto);
  }

  @Test
  @DisplayName("findArtistByExternalId() should call artist repository")
  void find_by_discogs_id_should_call_artist_repository() {
    // given
    when(artistRepository.findByExternalId(anyString())).thenReturn(Optional.of(artistEntity));

    // when
    underTest.findArtistByExternalId(EXTERNAL_ID);

    // then
    verify(artistRepository, times(1)).findByExternalId(EXTERNAL_ID);
  }

  @Test
  @DisplayName("findArtistByExternalId() should call artist dto transformer")
  void find_by_discogs_id_should_call_artist_dto_transformer() {
    // given
    when(artistRepository.findByExternalId(anyString())).thenReturn(Optional.of(artistEntity));

    // when
    underTest.findArtistByExternalId(EXTERNAL_ID);

    // then
    verify(artistTransformer, times(1)).transform(artistEntity);
  }

  @Test
  @DisplayName("findArtistByExternalId() should return an empty optional if artist does not exist")
  void find_by_discogs_id_should_return_empty_optional() {
    // when
    Optional<ArtistDto> artistOptional = underTest.findArtistByExternalId(EXTERNAL_ID);

    // then
    assertThat(artistOptional).isEmpty();
  }

  @Test
  @DisplayName("findAllByArtistExternalIdIn() should return all given entities that exist")
  void find_all_by_discogs_ids_should_return_all_entities_that_exist() {
    // given
    when(artistRepository.findAllByExternalIdIn(any())).thenReturn(List.of(artistEntity));
    when(artistTransformer.transform(any())).thenReturn(ArtistDtoFactory.createDefault());

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
    when(artistRepository.existsByExternalId(any())).thenReturn(true);

    // when
    boolean exists = underTest.existsArtistByExternalId(EXTERNAL_ID);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsArtistByExternalId() should return false if given entity does not exist")
  void exists_by_discogs_id_should_return_false() {
    // given
    when(artistRepository.existsByExternalId(any())).thenReturn(false);

    // when
    boolean exists = underTest.existsArtistByExternalId(EXTERNAL_ID);

    // then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("existsArtistByExternalId() should call artist repository")
  void exists_by_discogs_id_should_call_artist_repository() {
    // when
    underTest.existsArtistByExternalId(EXTERNAL_ID);

    // then
    verify(artistRepository, times(1)).existsByExternalId(EXTERNAL_ID);
  }

  @Test
  @DisplayName("Should pass provided arguments to discogs service")
  void searchDiscogsByName_should_pass_arguments() {
    // given
    var artistQueryString = "the query";
    var pageable = PageRequest.of(1, 10);
    doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
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
    doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
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
    discogsSearchResults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
    doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
    when(userEntityMock.isFollowing(anyString())).then(invocationOnMock -> {
      String artistId = invocationOnMock.getArgument(0);
      return artistId.equals("1") || artistId.equals("3");
    });
    doReturn(discogsSearchResults).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());

    // when
    var searchResults = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

    // then
    assertThat(searchResults.getSearchResults().get(0).isFollowed()).isTrue();
    assertThat(searchResults.getSearchResults().get(1).isFollowed()).isFalse();
    assertThat(searchResults.getSearchResults().get(2).isFollowed()).isTrue();
  }

  @Test
  @DisplayName("Should get current user")
  void searchDiscogsByName_should_get_user() {
    // given
    var artistQueryString = "the query";
    var pageable = PageRequest.of(1, 10);
    var userId = "userId";
    doReturn(userId).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
    doReturn(DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());

    // when
    underTest.searchDiscogsByName(artistQueryString, pageable);

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
    verify(userRepository, times(1)).findByPublicId(userId);
  }

  @Test
  @DisplayName("Should throw exception when user not found")
  void searchDiscogsByName_should_throw_exception() {
    // given
    var artistQueryString = "the query";
    var pageable = PageRequest.of(1, 10);
    var userId = "userId";
    doReturn(userId).when(currentPublicUserIdSupplier).get();
    doThrow(new ResourceNotFoundException(userId)).when(userRepository).findByPublicId(anyString());

    // when
    var throwable = catchThrowable(() -> underTest.searchDiscogsByName(artistQueryString, pageable));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(userId);
  }

  private List<DiscogsArtistSearchResultEntryDto> createListOfSearchResultEntries(List<String> externalIds) {
    return List.of(
        DiscogsArtistSearchResultEntryDtoFactory.withId(externalIds.get(0)),
        DiscogsArtistSearchResultEntryDtoFactory.withId(externalIds.get(1)),
        DiscogsArtistSearchResultEntryDtoFactory.withId(externalIds.get(2))
    );
  }
}
