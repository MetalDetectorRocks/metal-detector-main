package rocks.metaldetector.butler.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.EnumPrettyPrinter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ButlerReleaseResponseTransformer {

  private final EnumPrettyPrinter enumPrettyPrinter;

  static final String STATE_NOT_SET = "NOT_SET";

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
            .type(enumPrettyPrinter.prettyPrintEnumValue(release.getType()))
            .metalArchivesArtistUrl(release.getMetalArchivesArtistUrl())
            .metalArchivesAlbumUrl(release.getMetalArchivesAlbumUrl())
            .source(enumPrettyPrinter.prettyPrintEnumValue(release.getSource()))
            .state(enumPrettyPrinter.prettyPrintEnumValue(STATE_NOT_SET))
            .coverUrl(release.getCoverUrl())
            .build();
  }
}
