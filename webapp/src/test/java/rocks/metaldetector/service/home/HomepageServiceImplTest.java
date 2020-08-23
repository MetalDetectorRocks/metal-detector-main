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
  @DisplayName("releaseService is called with followed artists' names")
  void test_release_service_called_with_followed_artists_names() {
    // given
    var followedArtists = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    var followedArtistsNames = List.of("A", "B");
    doReturn(followedArtists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.createHomeResponse();

    // then
    verify(releaseService, times(1)).findReleases(eq(followedArtistsNames), any(), any());
  }

  @Test
  @DisplayName("releaseService is called with correct dates")
  void test_release_service_called_with_correct_dates() {
    // given
    var now = LocalDate.now();
    var expectedTimeRange = new TimeRange(now, now.plusMonths(6));
    var followedArtists = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    doReturn(followedArtists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.createHomeResponse();

    // then
    verify(releaseService, times(1)).findReleases(any(), eq(expectedTimeRange), any());
  }

  @Test
  @DisplayName("releaseService is called with a fixed page request")
  void test_release_service_called_with_fixed_page_request() {
    // given
    var expectedPageRequest = new PageRequest(1, 4);
    var followedArtists = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    doReturn(followedArtists).when(followArtistService).getFollowedArtistsOfCurrentUser();

    // when
    underTest.createHomeResponse();

    // then
    verify(releaseService, times(1)).findReleases(any(), any(), eq(expectedPageRequest));
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
    doReturn(List.of(ArtistDtoFactory.createDefault())).when(followArtistService).getFollowedArtistsOfCurrentUser();
    doReturn(upcomingReleases).when(releaseService).findReleases(any(), any(), any());

    // when
    var result = underTest.createHomeResponse();

    // then
    assertThat(result.getUpcomingReleases()).isEqualTo(upcomingReleases);
  }
}
