package rocks.metaldetector.butler.client.transformer;

import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerRelease;
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
            .type(WordUtils.capitalizeFully(release.getType()))
            .metalArchivesArtistUrl(release.getMetalArchivesArtistUrl())
            .metalArchivesAlbumUrl(release.getMetalArchivesAlbumUrl())
            .source(WordUtils.capitalizeFully(release.getSource()))
            .state(WordUtils.capitalizeFully(release.getState()))
            .build();
  }
}
