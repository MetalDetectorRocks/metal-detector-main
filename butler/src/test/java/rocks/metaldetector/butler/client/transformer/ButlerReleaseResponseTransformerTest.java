package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.ButlerDtoFactory;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.util.List;

class ButlerReleaseResponseTransformerTest implements WithAssertions {

  private ButlerReleaseResponseTransformer underTest = new ButlerReleaseResponseTransformer();

  @Test
  @DisplayName("Should transform ButlerReleaseResponse to list of ReleaseDto")
  void should_transform() {
    // given
    ButlerRelease release = ButlerDtoFactory.ButlerReleaseFactory.createDefault();
    ButlerReleasesResponse response = ButlerReleasesResponse.builder().releases(List.of(release)).build();

    // when
    List<ReleaseDto> releaseDtos = underTest.transform(response);

    // then
    assertThat(releaseDtos).hasSize(1);

    ReleaseDto releaseDto = releaseDtos.get(0);

    assertThat(releaseDto.getArtist()).isEqualTo(release.getArtist());
    assertThat(releaseDto.getAdditionalArtists()).isEqualTo(release.getAdditionalArtists());
    assertThat(releaseDto.getAlbumTitle()).isEqualTo(release.getAlbumTitle());
    assertThat(releaseDto.getReleaseDate()).isEqualTo(release.getReleaseDate());
    assertThat(releaseDto.getEstimatedReleaseDate()).isEqualTo(release.getEstimatedReleaseDate());
    assertThat(releaseDto.getGenre()).isEqualTo(release.getGenre());
    assertThat(releaseDto.getType()).isEqualTo(release.getType().toString());
    assertThat(releaseDto.getMetalArchivesArtistUrl()).isEqualTo(release.getMetalArchivesArtistUrl());
    assertThat(releaseDto.getMetalArchivesAlbumUrl()).isEqualTo(release.getMetalArchivesAlbumUrl());
    assertThat(releaseDto.getSource()).isEqualTo(release.getSource().toString());
    assertThat(releaseDto.getState()).isEqualTo(release.getState().toString());
  }
}