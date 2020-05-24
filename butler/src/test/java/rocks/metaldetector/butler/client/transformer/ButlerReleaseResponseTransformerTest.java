package rocks.metaldetector.butler.client.transformer;

import org.apache.commons.text.WordUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleaseFactory;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.EnumPrettyPrinter;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer.STATE_NOT_SET;

@ExtendWith(MockitoExtension.class)
class ButlerReleaseResponseTransformerTest implements WithAssertions {

  @Spy
  private EnumPrettyPrinter enumPrettyPrinter;

  @InjectMocks
  private ButlerReleaseResponseTransformer underTest;

  @Test
  @DisplayName("Should transform ButlerReleaseResponse to list of ReleaseDto")
  void should_transform() {
    // given
    ButlerRelease release = ButlerReleaseFactory.createDefault();
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
    assertThat(releaseDto.getType()).isEqualTo(WordUtils.capitalizeFully(release.getType()));
    assertThat(releaseDto.getMetalArchivesArtistUrl()).isEqualTo(release.getMetalArchivesArtistUrl());
    assertThat(releaseDto.getMetalArchivesAlbumUrl()).isEqualTo(release.getMetalArchivesAlbumUrl());
    assertThat(releaseDto.getSource()).isEqualTo(WordUtils.capitalizeFully(release.getSource()));
    assertThat(releaseDto.getState()).isEqualTo(WordUtils.capitalizeFully(release.getState()));
  }

  @Test
  @DisplayName("Should use enum pretty printer to transform enum values")
  void should_use_enum_pretty_printer() {
    // given
    ButlerRelease release = ButlerReleaseFactory.createDefault();
    ButlerReleasesResponse response = ButlerReleasesResponse.builder().releases(List.of(release)).build();

    // when
    underTest.transform(response);

    // then
    verify(enumPrettyPrinter, times(1)).prettyPrintEnumValue(eq(release.getType()));
    verify(enumPrettyPrinter, times(1)).prettyPrintEnumValue(eq(release.getSource()));
    verify(enumPrettyPrinter, times(1)).prettyPrintEnumValue(eq(STATE_NOT_SET));
  }
}
