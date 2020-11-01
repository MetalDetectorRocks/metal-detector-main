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
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.artist.ArtistService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.SpotifyAlbumDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.SpotifyArtistDtoFactory;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistImportServiceImplTest implements WithAssertions {

  @Mock
  private SpotifyService spotifyService;

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private ArtistService artistService;

  @InjectMocks
  private SpotifyArtistImportServiceImpl underTest;

  private final UserEntity userEntity = UserEntityFactory.createUser("user", "user@mail.com");

  @BeforeEach
  void setup() {
    userEntity.setSpotifyAuthorization(new SpotifyAuthorizationEntity("state"));
    doReturn(userEntity).when(currentUserSupplier).get();
  }

  @AfterEach
  void tearDown() {
    reset(spotifyService, currentUserSupplier, followArtistService, artistService);
  }

  @Test
  @DisplayName("currentUserSupplier is called")
  void test_current_user_supplier_called() {
    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(currentUserSupplier).get();
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
    verify(spotifyService).fetchLikedAlbums(accessToken);
  }

  @Test
  @DisplayName("artistService is called to get new artist ids")
  void test_artist_service_called() {
    // given
    var albumDto = SpotifyAlbumDtoFactory.createDefault();
    doReturn(List.of(albumDto)).when(spotifyService).fetchLikedAlbums(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService).findNewArtistIds(List.of(albumDto.getArtists().get(0).getId()));
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
    doReturn(List.of(album)).when(spotifyService).fetchLikedAlbums(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService).findNewArtistIds(List.of(id1, id2));
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
    doReturn(albumDtos).when(spotifyService).fetchLikedAlbums(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService).findNewArtistIds(List.of(id));
  }

  @Test
  @DisplayName("spotifyService is called to fetch artists")
  void test_spotify_service_called_with_new_artists_list() {
    // given
    var artists = List.of("id1", "id2");
    doReturn(artists).when(artistService).findNewArtistIds(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(spotifyService).searchArtistsByIds(artists);
  }

  @Test
  @DisplayName("artistService is called to persists all fetched artists")
  void test_artist_service_called_to_persist_new_artists() {
    // given
    var spotifyDtos = List.of(SpotifyArtistDtoFactory.withArtistName("a"));
    doReturn(spotifyDtos).when(spotifyService).searchArtistsByIds(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService).persistArtists(spotifyDtos);
  }

  @Test
  @DisplayName("artistService is called to get all imported artists after persisting")
  void test_artist_service_gets_all_artists() {
    // given
    var albumDto = SpotifyAlbumDtoFactory.createDefault();
    doReturn(List.of(albumDto)).when(spotifyService).fetchLikedAlbums(any());

    // when
    underTest.importArtistsFromLikedReleases();

    // then
    verify(artistService).findAllArtistsByExternalIds(List.of(albumDto.getArtists().get(0).getId()));
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
    verify(followArtistService).isCurrentUserFollowing(artistDtos.get(0).getExternalId(), SPOTIFY);
    verify(followArtistService).isCurrentUserFollowing(artistDtos.get(1).getExternalId(), SPOTIFY);
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
    verify(followArtistService).follow(artistDtos.get(0).getExternalId(), SPOTIFY);
    verify(followArtistService).follow(artistDtos.get(1).getExternalId(), SPOTIFY);
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
