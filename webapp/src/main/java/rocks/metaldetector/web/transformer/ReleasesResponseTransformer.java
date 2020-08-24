package rocks.metaldetector.web.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.web.api.response.ReleasesResponse;

@Service
@AllArgsConstructor
public class ReleasesResponseTransformer {

  public ReleasesResponse transform(ReleaseDto release) {
    return ReleasesResponse.builder()
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
            .build();
  }
}
