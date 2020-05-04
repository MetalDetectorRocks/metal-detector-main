package rocks.metaldetector.web.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.follow.FollowArtistService;
import rocks.metaldetector.web.api.response.DetectorReleasesResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DetectorReleasesResponseTransformer {

  private final FollowArtistService followArtistService;

  public List<DetectorReleasesResponse> transformListOf(List<ReleaseDto> releases) {
    var followedArtistsNames = followArtistService.findFollowedArtists().stream().map(ArtistDto::getArtistName).collect(Collectors.toList());
    return releases.stream()
                   .map(release -> transform(release, followedArtistsNames))
                   .collect(Collectors.toList());
  }

  private DetectorReleasesResponse transform(ReleaseDto release, List<String> followedArtistsNames) {
    return DetectorReleasesResponse.builder()
            .artist(release.getArtist())
            .albumTitle(release.getAlbumTitle())
            .releaseDate(release.getReleaseDate())
            .additionalArtists(release.getAdditionalArtists())
            .estimatedReleaseDate(release.getEstimatedReleaseDate())
            .genre(release.getGenre())
            .type(release.getType())
            .metalArchivesArtistUrl(release.getMetalArchivesArtistUrl())
            .metalArchivesAlbumUrl(release.getMetalArchivesAlbumUrl())
            .source(release.getSource())
            .state(release.getState())
            .isFollowed(followedArtistsNames.contains(release.getArtist()))
            .build();
  }
}
