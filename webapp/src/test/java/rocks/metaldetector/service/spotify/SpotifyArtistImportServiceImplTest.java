package rocks.metaldetector.service.spotify;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.service.SlicingService;
import rocks.metaldetector.service.artist.ArtistService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.SpotifyAlbumDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.SpotifyArtistDtoFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.service.spotify.SpotifyArtistImportServiceImpl.PAGE_SIZE;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistImportServiceImplTest implements WithAssertions {

  @Mock
  private SpotifyService spotifyService;

  @Mock
  private CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @Mock
  private UserRepository userRepository;

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private ArtistService artistService;

  @Mock
  private SlicingService slicingService;

  @InjectMocks
  private SpotifyArtistImportServiceImpl underTest;

  private UserEntity userEntity = UserEntityFactory.createUser("user", "user@mail.com");

  @BeforeEach
  void setup() {
    userEntity = UserEntityFactory.createUser("user", "user@mail.com");
    userEntity.setSpotifyAuthorization(new SpotifyAuthorizationEntity("state"));
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());
  }

  @AfterEach
  void tearDown() {
    reset(spotifyService, currentPublicUserIdSupplier, userRepository, followArtistService, artistService, slicingService);
  }

  @Test
  @DisplayName("currentUserSupplier is called")
  void test_current_user_supplier_called() {
    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
  }

  @Test
  @DisplayName("userRepository is called to get current user")
  void test_user_repository_called() {
    // given
    var publicUserId = "publicUserId";
    doReturn(publicUserId).when(currentPublicUserIdSupplier).get();

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(userRepository, times(1)).findByPublicId(publicUserId);
  }

  @Test
  @DisplayName("userRepository throws exception when userId is not found")
  void test_user_repository_throws_exception() {
    // given
    var publicUserId = "publicUserId";
    doThrow(new ResourceNotFoundException(publicUserId)).when(userRepository).findByPublicId(any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.importArtistsFromLikedReleases());

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(publicUserId);
  }

  @Test
  @DisplayName("spotifyService is called with access token")
  void test_spotify_service_called() {
    // given
    var accessToken = "accessToken";
    userEntity.getSpotifyAuthorization().setAccessToken(accessToken);

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(spotifyService, times(1)).importAlbums(accessToken);
  }

  @Test
  @DisplayName("artistService is called to get new artist ids")
  void test_artist_service_called() {
    // given
    var albumDto = SpotifyAlbumDtoFactory.createDefault();
    doReturn(List.of(albumDto)).when(spotifyService).importAlbums(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService, times(1)).findNewArtistIds(List.of(albumDto.getArtists().get(0).getId()));
  }

  @Test
  @DisplayName("artistService is called with all artists of an album")
  void test_artist_service_called_with_all_artists() {
    // given
    var album = SpotifyAlbumDtoFactory.withTwoArtist();
    var id1 = "id1";
    var id2 = "id2";
    album.getArtists().get(0).setId(id1);
    album.getArtists().get(1).setId(id2);
    doReturn(List.of(album)).when(spotifyService).importAlbums(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService, times(1)).findNewArtistIds(List.of(id1, id2));
  }

  @Test
  @DisplayName("duplicates are eliminated when calling artistService")
  void test_no_duplicates_when_calling_artist_service() {
    // given
    var firstAlbum = SpotifyAlbumDtoFactory.createDefault();
    var secondAlbum = SpotifyAlbumDtoFactory.createDefault();
    var id = "id";
    firstAlbum.getArtists().get(0).setId(id);
    secondAlbum.getArtists().get(0).setId(id);
    var albumDtos = List.of(firstAlbum, secondAlbum);
    doReturn(albumDtos).when(spotifyService).importAlbums(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService, times(1)).findNewArtistIds(List.of(id));
  }

  @Test
  @DisplayName("slicingService is called for every page of new artists")
  void test_slicing_service_is_called() {
    // given
    var artists = IntStream.rangeClosed(1, 101).mapToObj(String::valueOf).collect(Collectors.toList());
    doReturn(artists).when(artistService).findNewArtistIds(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(slicingService, times(1)).slice(artists, 1, PAGE_SIZE);
    verify(slicingService, times(1)).slice(artists, 2, PAGE_SIZE);
    verify(slicingService, times(1)).slice(artists, 3, PAGE_SIZE);
  }

  @Test
  @DisplayName("spotifyService is called with slicingService's result to fetch artists")
  void test_spotify_service_called_with_sliced_list() {
    // given
    var artists = IntStream.rangeClosed(1, 101).mapToObj(String::valueOf).collect(Collectors.toList());
    doReturn(artists).when(artistService).findNewArtistIds(any());
    doReturn(artists).when(slicingService).slice(any(), anyInt(), anyInt());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(spotifyService, times(3)).searchArtistsByIds(artists);
  }

  @Test
  @DisplayName("artistService is called to persists all fetched artists")
  void test_artist_service_called_to_persist_new_artists() {
    // given
    var artists = IntStream.rangeClosed(1, 101).mapToObj(String::valueOf).collect(Collectors.toList());
    var spotifyDto = SpotifyArtistDtoFactory.withArtistName("a");
    doReturn(artists).when(artistService).findNewArtistIds(any());
    doReturn(artists).when(slicingService).slice(any(), anyInt(), anyInt());
    doReturn(List.of(spotifyDto)).when(spotifyService).searchArtistsByIds(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService, times(1)).persistArtists(List.of(spotifyDto, spotifyDto, spotifyDto));
  }

  @Test
  @DisplayName("artistService is called to get all imported artists after persisting")
  void test_artist_service_gets_all_artists() {
    // given
    var albumDto = SpotifyAlbumDtoFactory.createDefault();
    doReturn(List.of(albumDto)).when(spotifyService).importAlbums(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService, times(1)).findAllArtistsByExternalIds(List.of(albumDto.getArtists().get(0).getId()));
  }

  @Test
  @DisplayName("followArtistService is called to check for already followed artistDtos")
  void test_follow_artist_service_called_to_check_followed() {
    // given
    var artistDtos = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    doReturn(artistDtos).when(artistService).findAllArtistsByExternalIds(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(followArtistService, times(1)).isCurrentUserFollowing(artistDtos.get(0).getExternalId(), SPOTIFY);
    verify(followArtistService, times(1)).isCurrentUserFollowing(artistDtos.get(1).getExternalId(), SPOTIFY);
  }

  @Test
  @DisplayName("followArtistService is called to follow new artistDtos")
  void test_follow_artist_service_called_to_follow() {
    // given
    var artistDtos = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    doReturn(artistDtos).when(artistService).findAllArtistsByExternalIds(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(followArtistService, times(1)).follow(artistDtos.get(0).getExternalId(), SPOTIFY);
    verify(followArtistService, times(1)).follow(artistDtos.get(1).getExternalId(), SPOTIFY);
  }

  @Test
  @DisplayName("artistDtos are returned")
  void test_artist_dtos_are_returned() {
    // given
    var artistDtos = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    doReturn(artistDtos).when(artistService).findAllArtistsByExternalIds(any());

    // when
    var result = underTest.importArtistsFromLikedReleases();

    // then
    assertThat(result).isEqualTo(artistDtos);
  }
}
