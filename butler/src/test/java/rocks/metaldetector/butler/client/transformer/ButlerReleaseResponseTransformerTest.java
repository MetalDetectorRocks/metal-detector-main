package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.time.LocalDate;
import java.util.List;

class ButlerReleaseResponseTransformerTest implements WithAssertions {

  private ButlerReleaseResponseTransformer underTest = new ButlerReleaseResponseTransformer();

  @Test
  @DisplayName("Should transform ButlerReleaseResponse to list of ReleaseDto")
  void should_transform() {
    // given
    ButlerRelease release = ButlerRelease.builder()
            .artist("A")
            .albumTitle("B")
            .releaseDate(LocalDate.now())
            .additionalArtists(List.of("C"))
            .estimatedReleaseDate("Summer 2020")
            .build();
    ButlerReleasesResponse response = ButlerReleasesResponse.builder().releases(List.of(release)).build();

    // when
    List<ReleaseDto> releaseDtos = underTest.transform(response);

    // then
    assertThat(releaseDtos).hasSize(1);
    assertThat(releaseDtos.get(0)).isEqualTo(
            ReleaseDto.builder()
                    .artist(release.getArtist())
                    .albumTitle(release.getAlbumTitle())
                    .releaseDate(release.getReleaseDate())
                    .additionalArtists(release.getAdditionalArtists())
                    .estimatedReleaseDate(release.getEstimatedReleaseDate())
                    .build()
    );
  }
}