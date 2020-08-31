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
class ArtistsServiceImplTest implements WithAssertions {

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
  private ArtistsServiceImpl underTest;

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

  @Nested
  @DisplayName("Test discogs searching")
  class DiscogsSearchTest {

    @Test
    @DisplayName("Should pass provided arguments to discogs service")
    void searchDiscogsByName_should_pass_arguments() {
      // given
      var artistQueryString = "the query";
      var pageable = PageRequest.of(1, 10);
      doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      doReturn(DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
      doReturn(ArtistSearchResponseFactory.discogs()).when(searchResponseTransformer).transformDiscogs(any());

      // when
      underTest.searchDiscogsByName(artistQueryString, pageable);

      // then
      verify(discogsService, times(1)).searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
    }

    @Test
    @DisplayName("Should call searchResultTransformer with discogs result")
    void searchDiscogsByName_should_call_result_transformer() {
      // given
      var expectedSearchResults = DiscogsArtistSearchResultDtoFactory.createDefault();
      doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      doReturn(expectedSearchResults).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
      doReturn(ArtistSearchResponseFactory.discogs()).when(searchResponseTransformer).transformDiscogs(any());

      // when
      underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

      // then
      verify(searchResponseTransformer, times(1)).transformDiscogs(expectedSearchResults);
    }

    @Test
    @DisplayName("Should return searchResultTransformer's result")
    void searchDiscogsByName_should_return_results() {
      // given
      var expectedSearchResult = ArtistSearchResponseFactory.discogs();
      doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      doReturn(DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
      doReturn(expectedSearchResult).when(searchResponseTransformer).transformDiscogs(any());

      // when
      ArtistSearchResponse result = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

      // then
      assertThat(result).isEqualTo(expectedSearchResult);
    }

    @Test
    @DisplayName("Should mark all already followed artists")
    void searchDiscogsByName_should_mark_already_followed_artists() {
      // given
      var discogssearchresults = ArtistSearchResponseFactory.discogs();
      discogssearchresults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
      doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      when(userEntityMock.isFollowing(anyString())).then(invocationOnMock -> {
        String artistId = invocationOnMock.getArgument(0);
        return artistId.equals("1") || artistId.equals("3");
      });
      doReturn(discogssearchresults).when(searchResponseTransformer).transformDiscogs(any());

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
      doReturn(ArtistSearchResponseFactory.discogs()).when(searchResponseTransformer).transformDiscogs(any());

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

    private List<ArtistSearchResponseEntryDto> createListOfSearchResultEntries(List<String> externalIds) {
      return List.of(
          ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(0)),
          ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(1)),
          ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(2))
      );
    }
  }

  @Nested
  @DisplayName("Test spotify searching")
  class SpotifySearchTest {

    @Test
    @DisplayName("Should pass provided arguments to spotify service")
    void searchSpotifyByName_should_pass_arguments() {
      // given
      var artistQueryString = "the query";
      var pageable = PageRequest.of(1, 10);
      doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      doReturn(SpotifyArtistSearchResultDtoFactory.createDefault()).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
      doReturn(ArtistSearchResponseFactory.spotify()).when(searchResponseTransformer).transformSpotify(any());

      // when
      underTest.searchSpotifyByName(artistQueryString, pageable);

      // then
      verify(spotifyService, times(1)).searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
    }

    @Test
    @DisplayName("Should call searchResultTransformer with spotify result")
    void searchSpotifyByName_should_call_result_transformer() {
      // given
      var expectedSearchResults = SpotifyArtistSearchResultDtoFactory.createDefault();
      doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      doReturn(expectedSearchResults).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
      doReturn(ArtistSearchResponseFactory.spotify()).when(searchResponseTransformer).transformSpotify(any());

      // when
      underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

      // then
      verify(searchResponseTransformer, times(1)).transformSpotify(expectedSearchResults);
    }

    @Test
    @DisplayName("Should return searchResultTransformer's result")
    void searchSpotifyByName_should_return_results() {
      // given
      var expectedSearchResult = ArtistSearchResponseFactory.spotify();
      doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      doReturn(SpotifyArtistSearchResultDtoFactory.createDefault()).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
      doReturn(expectedSearchResult).when(searchResponseTransformer).transformSpotify(any());

      // when
      ArtistSearchResponse result = underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

      // then
      assertThat(result).isEqualTo(expectedSearchResult);
    }

    @Test
    @DisplayName("Should mark all already followed artists")
    void searchSpotifyByName_should_mark_already_followed_artists() {
      // given
      var spotifySearchResults = ArtistSearchResponseFactory.spotify();
      spotifySearchResults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
      doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      when(userEntityMock.isFollowing(anyString())).then(invocationOnMock -> {
        String artistId = invocationOnMock.getArgument(0);
        return artistId.equals("1") || artistId.equals("3");
      });
      doReturn(spotifySearchResults).when(searchResponseTransformer).transformSpotify(any());

      // when
      var searchResults = underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

      // then
      assertThat(searchResults.getSearchResults().get(0).isFollowed()).isTrue();
      assertThat(searchResults.getSearchResults().get(1).isFollowed()).isFalse();
      assertThat(searchResults.getSearchResults().get(2).isFollowed()).isTrue();
    }

    @Test
    @DisplayName("Should get current user")
    void searchSpotifyByName_should_get_user() {
      // given
      var artistQueryString = "the query";
      var pageable = PageRequest.of(1, 10);
      var userId = "userId";
      doReturn(userId).when(currentPublicUserIdSupplier).get();
      doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
      doReturn(SpotifyArtistSearchResultDtoFactory.createDefault()).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
      doReturn(ArtistSearchResponseFactory.spotify()).when(searchResponseTransformer).transformSpotify(any());

      // when
      underTest.searchSpotifyByName(artistQueryString, pageable);

      // then
      verify(currentPublicUserIdSupplier, times(1)).get();
      verify(userRepository, times(1)).findByPublicId(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void searchSpotifyByName_should_throw_exception() {
      // given
      var artistQueryString = "the query";
      var pageable = PageRequest.of(1, 10);
      var userId = "userId";
      doReturn(userId).when(currentPublicUserIdSupplier).get();
      doThrow(new ResourceNotFoundException(userId)).when(userRepository).findByPublicId(anyString());

      // when
      var throwable = catchThrowable(() -> underTest.searchSpotifyByName(artistQueryString, pageable));

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(userId);
    }

    private List<ArtistSearchResponseEntryDto> createListOfSearchResultEntries(List<String> externalIds) {
      return List.of(
          ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(0)),
          ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(1)),
          ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(2))
      );
    }
  }
}
