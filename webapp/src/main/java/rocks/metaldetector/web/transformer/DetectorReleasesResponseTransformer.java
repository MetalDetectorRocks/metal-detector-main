package rocks.metaldetector.web.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.web.api.response.DetectorReleasesResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DetectorReleasesResponseTransformer {

  private final CurrentUserSupplier currentUserSupplier;

  public List<DetectorReleasesResponse> transformListOf(List<ReleaseDto> releases) {
    var followedArtistsNames = currentUserSupplier.get().getFollowedArtists().stream().map(ArtistEntity::getArtistName).collect(Collectors.toList());
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
