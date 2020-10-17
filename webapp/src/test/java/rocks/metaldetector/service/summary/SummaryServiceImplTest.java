package rocks.metaldetector.service.summary;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SummaryServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseCollector releaseCollector;

  @Mock
  private ArtistCollector artistCollector;

  @Mock
  private FollowArtistService followArtistService;

  @InjectMocks
  private SummaryServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(releaseCollector, artistCollector, followArtistService);
  }

  @Test
  @DisplayName("followArtistService is called to get current user's followed artists")
  void test_follow_artist_service_called() {
    // when
    underTest.createSummaryResponse();

    // then
    verify(followArtistService, times(1)).getFollowedArtistsOfCurrentUser();
  }

  @Test
  @DisplayName("releaseCollector is called with followed artists to get upcoming releases")
  void test_release_collector_upcoming_releases() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());
    doReturn(artists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.createSummaryResponse();

    // then
    verify(releaseCollector, times(1)).collectUpcomingReleases(eq(artists));
  }

  @Test
  @DisplayName("releaseCollector is called with followed artists to get recent releases")
  void test_release_collector_recent_releases() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());
    doReturn(artists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.createSummaryResponse();

    // then
    verify(releaseCollector, times(1)).collectRecentReleases(eq(artists));
  }

  @Test
  @DisplayName("releaseCollector is called with top artists to get most expected releases")
  void test_release_collector_most_expected_releases() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());
    doReturn(artists).when(artistCollector).collectTopFollowedArtists();

    // when
    underTest.createSummaryResponse();

    // then
    verify(releaseCollector, times(1)).collectUpcomingReleases(eq(artists));
  }

  @Test
  @DisplayName("artistCollector is called to get top followed artists")
  void test_artist_collector_top_artists() {
    // when
    underTest.createSummaryResponse();

    // then
    verify(artistCollector, times(1)).collectTopFollowedArtists();
  }

  @Test
  @DisplayName("artistCollector is called to get recently followed artists")
  void test_artist_collector_recently_artists() {
    // when
    underTest.createSummaryResponse();

    // then
    verify(artistCollector, times(1)).collectRecentlyFollowedArtists();
  }

  @Test
  @DisplayName("upcoming releases are returned")
  void test_upcoming_releases_returned() {
    // given
    var releases = List.of(ReleaseDtoFactory.createDefault());
    doReturn(releases).when(releaseCollector).collectUpcomingReleases(anyList());

    // when
    var result = underTest.createSummaryResponse();

    // then
    assertThat(result.getUpcomingReleases()).isEqualTo(releases);
  }

  @Test
  @DisplayName("recent releases are returned")
  void test_recent_releases_returned() {
    // given
    var releases = List.of(ReleaseDtoFactory.createDefault());
    doReturn(releases).when(releaseCollector).collectRecentReleases(anyList());

    // when
    var result = underTest.createSummaryResponse();

    // then
    assertThat(result.getRecentReleases()).isEqualTo(releases);
  }

  @Test
  @DisplayName("top followed artists are returned")
  void test_top_followed_artists_returned() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());
    doReturn(artists).when(artistCollector).collectTopFollowedArtists();

    // when
    var result = underTest.createSummaryResponse();

    // then
    assertThat(result.getFavoriteCommunityArtists()).isEqualTo(artists);
  }

  @Test
  @DisplayName("most expected releases are returned")
  void test_most_expected_releases_returned() {
    // given
    var releases = List.of(ReleaseDtoFactory.createDefault());
    doReturn(Collections.emptyList()).when(releaseCollector).collectUpcomingReleases(anyList());
    doReturn(releases).when(releaseCollector).collectUpcomingReleases(anyList());

    // when
    var result = underTest.createSummaryResponse();

    // then
    assertThat(result.getMostExpectedReleases()).isEqualTo(releases);
  }

  @Test
  @DisplayName("recently followed artists are returned")
  void test_recently_followed_artists_returned() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());
    doReturn(artists).when(artistCollector).collectRecentlyFollowedArtists();

    // when
    var result = underTest.createSummaryResponse();

    // then
    assertThat(result.getRecentlyFollowedArtists()).isEqualTo(artists);
  }
}
