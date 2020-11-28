package rocks.metaldetector.butler.client.transformer;

import org.apache.commons.text.WordUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleaseFactory;
import rocks.metaldetector.butler.api.ButlerPagination;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.EnumPrettyPrinter;
import rocks.metaldetector.support.Page;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ButlerReleaseResponseTransformerTest implements WithAssertions {

  @Spy
  private EnumPrettyPrinter enumPrettyPrinter;

  @InjectMocks
  private ButlerReleaseResponseTransformer underTest;

  @Test
  @DisplayName("Should transform pagination infos from ButlerReleasesResponse to Pagination within Page of ReleaseDto")
  void should_transform_butler_releases_response_to_page_of_release_dto() {
    // given
    ButlerRelease release = ButlerReleaseFactory.createDefault();
    ButlerReleasesResponse response = ButlerReleasesResponse
            .builder()
            .pagination(new ButlerPagination(1, 10, 2, 20))
            .releases(List.of(release))
            .build();

    // when
    Page<ReleaseDto> resultPage = underTest.transformToPage(response);

    // then
    assertThat(resultPage.getPagination().getCurrentPage()).isEqualTo(1);
    assertThat(resultPage.getPagination().getItemsPerPage()).isEqualTo(10);
    assertThat(resultPage.getPagination().getTotalPages()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should use method transformToList to transform releases")
  void should_use_method_transform_to_list_to_transform_releases() {
    // given
    ButlerReleaseResponseTransformer transformer = Mockito.spy(new ButlerReleaseResponseTransformer(enumPrettyPrinter));
    ButlerRelease release = ButlerReleaseFactory.createDefault();
    ButlerReleasesResponse response = ButlerReleasesResponse.builder()
            .releases(List.of(release))
            .pagination(new ButlerPagination())
            .build();

    // when
    transformer.transformToPage(response);

    // then
    verify(transformer).transformToList(eq(response));
  }

  @Test
  @DisplayName("Should transform ButlerReleaseResponse to list of ReleaseDto")
  void should_transform() {
    // given
    ButlerRelease release = ButlerReleaseFactory.createDefault();
    ButlerReleasesResponse response = ButlerReleasesResponse.builder().releases(List.of(release)).build();

    // when
    List<ReleaseDto> releaseDtos = underTest.transformToList(response);

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
    assertThat(releaseDto.getArtistDetailsUrl()).isEqualTo(release.getArtistDetailsUrl());
    assertThat(releaseDto.getReleaseDetailsUrl()).isEqualTo(release.getReleaseDetailsUrl());
    assertThat(releaseDto.getSource()).isEqualTo(WordUtils.capitalizeFully(release.getSource()));
    assertThat(releaseDto.getState()).isEqualTo(WordUtils.capitalizeFully(release.getState()));
    assertThat(releaseDto.getCoverUrl()).isEqualTo(release.getCoverUrl());
  }

  @Test
  @DisplayName("Should use enum pretty printer to transform enum values")
  void should_use_enum_pretty_printer() {
    // given
    ButlerRelease release = ButlerReleaseFactory.createDefault();
    ButlerReleasesResponse response = ButlerReleasesResponse.builder().releases(List.of(release)).build();

    // when
    underTest.transformToList(response);

    // then
    verify(enumPrettyPrinter).prettyPrintEnumValue(eq(release.getType()));
    verify(enumPrettyPrinter).prettyPrintEnumValue(eq(release.getSource()));
    verify(enumPrettyPrinter).prettyPrintEnumValue(eq(release.getState()));
  }
}
