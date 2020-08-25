package rocks.metaldetector.service.home;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class HomepageServiceImplTest implements WithAssertions {

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private ReleaseService releaseService;

  @InjectMocks
  private HomepageServiceImpl underTest;

  @Test
  @DisplayName("followArtistService is called to get followed artists")
  void test_followed_artist_service_is_called() {
    // when
    underTest.createHomeResponse();

    // then
    verify(followArtistService, times(1)).getFollowedArtistsOfCurrentUser();
  }

  @Test
  @DisplayName("releaseService is called to get upcoming releases")
  void test_release_service_called_for_upcoming_releases() {
    // given
    var followedArtists = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    var followedArtistsNames = List.of("A", "B");
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now, now.plusMonths(6));
    var expectedPageRequest = new PageRequest(1, 4);
    doReturn(followedArtists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.createHomeResponse();

    // then
    verify(releaseService, times(1)).findReleases(eq(followedArtistsNames), eq(expectedTimeRange), eq(expectedPageRequest));
  }

  @Test
  @DisplayName("releaseService is called to get recent releases")
  void test_release_service_called_for_recent_releases() {
    // given
    var followedArtists = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    var followedArtistsNames = List.of("A", "B");
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now.minusMonths(6), now);
    var expectedPageRequest = new PageRequest(1, 4);
    doReturn(followedArtists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.createHomeResponse();

    // then
    verify(releaseService, times(1)).findReleases(eq(followedArtistsNames), eq(expectedTimeRange), eq(expectedPageRequest));
  }

  @Test
  @DisplayName("releaseService is not called with user does not follow any artists")
  void test_release_service_not_called() {
    // given
    doReturn(Collections.emptyList()).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.createHomeResponse();

    // then
    verifyNoInteractions(releaseService);
  }

  @Test
  @DisplayName("upcoming releases are returned")
  void test_upcoming_releases_are_returned() {
    // given
    List<ReleaseDto> upcomingReleases = List.of(ReleaseDtoFactory.withArtistName("A"), ReleaseDtoFactory.withArtistName("B"));
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now, now.plusMonths(6));
    doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfCurrentUser();
    doReturn(upcomingReleases).when(releaseService).findReleases(any(), eq(expectedTimeRange), any());

    // when
    var result = underTest.createHomeResponse();

    // then
    assertThat(result.getUpcomingReleases()).isEqualTo(upcomingReleases);
  }

  @Test
  @DisplayName("recent releases are returned")
  void test_recent_releases_are_returned() {
    // given
    List<ReleaseDto> recentReleases = List.of(ReleaseDtoFactory.withArtistName("A"), ReleaseDtoFactory.withArtistName("B"));
    var now = LocalDate.now();
    var expectedTimeRangeRecent = new TimeRange(now.minusMonths(6), now);
    var expectedTimeRangeUpcoming = new TimeRange(now, now.plusMonths(6));
    doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfCurrentUser();
    doReturn(recentReleases).when(releaseService).findReleases(any(), eq(expectedTimeRangeRecent), any());
    doReturn(Collections.emptyList()).when(releaseService).findReleases(any(), eq(expectedTimeRangeUpcoming), any());

    // when
    var result = underTest.createHomeResponse();

    // then
    assertThat(result.getRecentReleases()).isEqualTo(recentReleases);
  }
}
