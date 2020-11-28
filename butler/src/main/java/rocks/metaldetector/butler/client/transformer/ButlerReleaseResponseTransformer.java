package rocks.metaldetector.butler.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerPagination;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.EnumPrettyPrinter;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ButlerReleaseResponseTransformer {

  private final EnumPrettyPrinter enumPrettyPrinter;

  public Page<ReleaseDto> transformToPage(ButlerReleasesResponse response) {
    ButlerPagination butlerPagination = response.getPagination();
    Pagination pagination = new Pagination(butlerPagination.getTotalPages(), butlerPagination.getCurrentPage(), butlerPagination.getSize());
    return new Page<>(transformToList(response), pagination);
  }

  public List<ReleaseDto> transformToList(ButlerReleasesResponse response) {
    return response.getReleases().stream().map(this::transformRelease).collect(Collectors.toList());
  }

  private ReleaseDto transformRelease(ButlerRelease release) {
    return ReleaseDto.builder()
            .id(release.getId())
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
            .state(enumPrettyPrinter.prettyPrintEnumValue(release.getState()))
            .coverUrl(release.getCoverUrl())
            .build();
  }
}
