package rocks.metaldetector.service.summary;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.web.api.response.SummaryResponse;

import java.util.List;

@Service
@Profile({"default", "preview", "prod"})
@AllArgsConstructor
public class SummaryServiceImpl implements SummaryService {

  public static final int RESULT_LIMIT = 4;
  public static final int TIME_RANGE_MONTHS = 6;

  private final ReleaseCollector releaseCollector;
  private final ArtistCollector artistCollector;
  private final FollowArtistService followArtistService;

  @Override
  public SummaryResponse createSummaryResponse() {
    List<ArtistDto> currentUsersFollowedArtists = followArtistService.getFollowedArtistsOfCurrentUser();
    List<ArtistDto> mostFollowedArtists = artistCollector.collectTopFollowedArtists();
    List<ArtistDto> recentlyFollowedArtists = artistCollector.collectRecentlyFollowedArtists();

    List<ReleaseDto> upcomingReleases = releaseCollector.collectUpcomingReleases(currentUsersFollowedArtists);
    List<ReleaseDto> recentReleases = releaseCollector.collectRecentReleases(currentUsersFollowedArtists);
    List<ReleaseDto> mostExpectedReleases = releaseCollector.collectUpcomingReleases(mostFollowedArtists);

    return SummaryResponse.builder()
        .upcomingReleases(upcomingReleases)
        .recentReleases(recentReleases)
        .favoriteCommunityArtists(mostFollowedArtists)
        .mostExpectedReleases(mostExpectedReleases)
        .recentlyFollowedArtists(recentlyFollowedArtists)
        .build();
  }
}
