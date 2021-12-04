package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DashboardResponse {

  private List<ReleaseDto> upcomingReleases;
  private List<ReleaseDto> recentReleases;
  private List<ReleaseDto> mostExpectedReleases;
  private List<ArtistDto> recentlyFollowedArtists;
  private List<ArtistDto> favoriteCommunityArtists;
}
