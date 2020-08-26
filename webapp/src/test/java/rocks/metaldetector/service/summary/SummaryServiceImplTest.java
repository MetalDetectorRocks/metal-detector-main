package rocks.metaldetector.service.summary;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.testutil.DtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.util.List;

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

  @InjectMocks
  private SummaryServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(releaseCollector, artistCollector);
  }

  @Test
  @DisplayName("releaseCollector is called to get upcoming releases")
  void test_release_collector_upcoming_releases() {
    // when
    underTest.createSummaryResponse();

    // then
    verify(releaseCollector, times(1)).collectUpcomingReleases();
  }

  @Test
  @DisplayName("releaseCollector is called to get recent releases")
  void test_release_collector_recent_releases() {
    // when
    underTest.createSummaryResponse();

    // then
    verify(releaseCollector, times(1)).collectRecentReleases();
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
  @DisplayName("upcoming releases are returned")
  void test_upcoming_releases_returned() {
    // given
    var releases = List.of(ReleaseDtoFactory.createDefault());
    doReturn(releases).when(releaseCollector).collectUpcomingReleases();

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
    doReturn(releases).when(releaseCollector).collectRecentReleases();

    // when
    var result = underTest.createSummaryResponse();

    // then
    assertThat(result.getRecentReleases()).isEqualTo(releases);
  }

  @Test
  @DisplayName("top followed artists are returned")
  void test_top_followed_artists_returned() {
    // given
    var artists = List.of(DtoFactory.ArtistDtoFactory.createDefault());
    doReturn(artists).when(artistCollector).collectTopFollowedArtists();

    // when
    var result = underTest.createSummaryResponse();

    // then
    assertThat(result.getFavoriteCommunityArtists()).isEqualTo(artists);
  }
}
