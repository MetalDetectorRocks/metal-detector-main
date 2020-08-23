package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

class ReleasesResponseTransformerTest implements WithAssertions {

  private final ReleasesResponseTransformer underTest = new ReleasesResponseTransformer();

  @Test
  @DisplayName("Should transform ReleasesResponseDto")
  void should_transform() {
    // given
    var releaseDto = ReleaseDtoFactory.withArtistName("Metallica");

    // when
    var response = underTest.transform(releaseDto);

    // then
    assertThat(response.getArtist()).isEqualTo(releaseDto.getArtist());
    assertThat(response.getAlbumTitle()).isEqualTo(releaseDto.getAlbumTitle());
    assertThat(response.getReleaseDate()).isEqualTo(releaseDto.getReleaseDate());
    assertThat(response.getAdditionalArtists()).isEqualTo(releaseDto.getAdditionalArtists());
    assertThat(response.getGenre()).isEqualTo(releaseDto.getGenre());
    assertThat(response.getType()).isEqualTo(releaseDto.getType());
    assertThat(response.getMetalArchivesArtistUrl()).isEqualTo(releaseDto.getMetalArchivesArtistUrl());
    assertThat(response.getMetalArchivesAlbumUrl()).isEqualTo(releaseDto.getMetalArchivesAlbumUrl());
    assertThat(response.getSource()).isEqualTo(releaseDto.getSource());
    assertThat(response.getState()).isEqualTo(releaseDto.getState());
  }
}
