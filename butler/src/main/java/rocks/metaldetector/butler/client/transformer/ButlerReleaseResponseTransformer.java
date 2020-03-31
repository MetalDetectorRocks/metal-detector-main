package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleaseEntityRecordState;
import rocks.metaldetector.butler.api.ButlerReleaseSource;
import rocks.metaldetector.butler.api.ButlerReleaseType;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ButlerReleaseResponseTransformer {

  public List<ReleaseDto> transform(ButlerReleasesResponse response) {
    return response.getReleases().stream().map(this::transformRelease).collect(Collectors.toList());
  }

  private ReleaseDto transformRelease(ButlerRelease release) {
    return ReleaseDto.builder()
            .artist(release.getArtist())
            .albumTitle(release.getAlbumTitle())
            .additionalArtists(release.getAdditionalArtists())
            .releaseDate(release.getReleaseDate())
            .estimatedReleaseDate(release.getEstimatedReleaseDate())
            .genre(release.getGenre())
            .type(transformReleaseType(release.getType()))
            .metalArchivesArtistUrl(release.getMetalArchivesArtistUrl())
            .metalArchivesAlbumUrl(release.getMetalArchivesAlbumUrl())
            .source(transformReleaseSource(release.getSource()))
            .state(transformReleaseState(release.getState()))
            .build();
  }

  private String transformReleaseType(ButlerReleaseType type) {
    return type != null ? type.toDisplayString() : null;
  }

  private String transformReleaseSource(ButlerReleaseSource source) {
    return source != null ? source.toDisplayString() : null;
  }

  private String transformReleaseState(ButlerReleaseEntityRecordState state) {
    return state != null ? state.toDisplayString() : null;
  }
}
