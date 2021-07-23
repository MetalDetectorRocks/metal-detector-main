package rocks.metaldetector.service.spotify;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.testutil.DtoFactory.SpotifyAlbumDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.SpotifyArtistDtoFactory;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.service.spotify.SpotifyFetchType.ALBUMS;
import static rocks.metaldetector.service.spotify.SpotifyFetchType.ARTISTS;

@ExtendWith(MockitoExtension.class)
class SpotifySynchronizationServiceImplTest implements WithAssertions {

  @Mock
  private SpotifyService spotifyService;

  @Mock
  private FollowArtistService followArtistService;

  @InjectMocks
  private SpotifySynchronizationServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(spotifyService, followArtistService);
  }

  @Nested
  @DisplayName("Tests for artist synchronization")
  class SynchronizeArtistsTest {

    @Test
    @DisplayName("followArtistService is called with spotify artist ids")
    void test_follow_artist_service_is_called() {
      // given
      List<String> spotifyArtistIds = List.of("abc", "def");

      // when
      underTest.synchronizeArtists(spotifyArtistIds);

      // then
      verify(followArtistService).followSpotifyArtists(spotifyArtistIds);
    }

    @Test
    @DisplayName("result from followArtistService is returned")
    void test_follow_artist_service_result_is_returned() {
      // given
      var artistCount = 666;
      doReturn(artistCount).when(followArtistService).followSpotifyArtists(any());

      // when
      var result = underTest.synchronizeArtists(Collections.emptyList());

      // then
      assertThat(result).isEqualTo(artistCount);
    }
  }

  @Nested
  @DisplayName("Tests for getting saved artists")
  class FetchSavedArtistsTest {

    @Test
    @DisplayName("ALBUMS: duplicates are eliminated from liked albums before the spotify service is called")
    void test_no_duplicates_when_calling_artist_service() {
      // given
      var firstAlbum = SpotifyAlbumDtoFactory.createDefault();
      var secondAlbum = SpotifyAlbumDtoFactory.createDefault();
      var id = "id";

      firstAlbum.getArtists().get(0).setId(id);
      secondAlbum.getArtists().get(0).setId(id);
      var albumDtos = List.of(firstAlbum, secondAlbum);
      doReturn(albumDtos).when(spotifyService).fetchLikedAlbums();

      // when
      underTest.fetchSavedArtists(List.of(ALBUMS));

      // then
      verify(spotifyService).searchArtistsByIds(List.of(firstAlbum.getArtists().get(0).getId()));
    }

    @Test
    @DisplayName("results from both sources are returned")
    void test_both_sources() {
      // given
      var artistA = SpotifyArtistDtoFactory.withArtistName("a");
      var artistB = SpotifyArtistDtoFactory.withArtistName("b");
      doReturn(false).when(followArtistService).isCurrentUserFollowing(any(), any());
      doReturn(List.of(artistA)).when(spotifyService).fetchFollowedArtists();
      doReturn(List.of(artistB)).when(spotifyService).searchArtistsByIds(any());

      // when
      var result = underTest.fetchSavedArtists(List.of(ARTISTS, ALBUMS));

      // then
      assertThat(result).containsExactly(artistA, artistB);
    }

    @Test
    @DisplayName("duplicates are removed over both sources")
    void test_duplicates_removed() {
      // given
      var artistA = SpotifyArtistDtoFactory.withArtistName("a");
      var artistADuplicate = SpotifyArtistDtoFactory.withArtistName("a");
      doReturn(false).when(followArtistService).isCurrentUserFollowing(any(), any());
      doReturn(List.of(artistADuplicate)).when(spotifyService).fetchFollowedArtists( );
      doReturn(List.of(artistA)).when(spotifyService).searchArtistsByIds(any());

      // when
      var result = underTest.fetchSavedArtists(List.of(ARTISTS, ALBUMS));

      // then
      assertThat(result).containsExactly(artistA);
    }

    @Test
    @DisplayName("followArtistService is called for each artist")
    void test_follow_artist_service_called_to_check_followed() {
      // given
      var spotifyArtist1 = SpotifyArtistDtoFactory.withArtistName("Slayer");
      var spotifyArtist2 = SpotifyArtistDtoFactory.withArtistName("Metallica");
      doReturn(List.of(SpotifyAlbumDtoFactory.createDefault())).when(spotifyService).fetchLikedAlbums();
      doReturn(List.of(spotifyArtist1, spotifyArtist2)).when(spotifyService).searchArtistsByIds(anyList());

      // when
      underTest.fetchSavedArtists(List.of(ALBUMS));

      // then
      verify(followArtistService).isCurrentUserFollowing(spotifyArtist1.getId(), SPOTIFY);
      verify(followArtistService).isCurrentUserFollowing(spotifyArtist2.getId(), SPOTIFY);
    }

    @Test
    @DisplayName("Spotify artists are returned in alphabetically order")
    void test_spotify_artist_are_returned() {
      // given
      var spotifyArtist1 = SpotifyArtistDtoFactory.withArtistName("B");
      var spotifyArtist2 = SpotifyArtistDtoFactory.withArtistName("C");
      var spotifyArtist3 = SpotifyArtistDtoFactory.withArtistName("A");
      doReturn(List.of(SpotifyAlbumDtoFactory.createDefault())).when(spotifyService).fetchLikedAlbums();
      doReturn(List.of(spotifyArtist1, spotifyArtist2, spotifyArtist3)).when(spotifyService).searchArtistsByIds(anyList());

      // when
      var result = underTest.fetchSavedArtists(List.of(ALBUMS));

      // then
      assertThat(result).isEqualTo(List.of(spotifyArtist3, spotifyArtist1, spotifyArtist2));
    }
  }
}
