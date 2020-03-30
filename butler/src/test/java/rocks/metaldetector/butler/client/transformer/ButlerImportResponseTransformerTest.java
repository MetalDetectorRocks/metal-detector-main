package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.api.ButlerImportResponse;
import rocks.metaldetector.butler.facade.dto.ImportResultDto;

class ButlerImportResponseTransformerTest implements WithAssertions {

  private ButlerImportResponseTransformer underTest = new ButlerImportResponseTransformer();

  @Test
  @DisplayName("Should transform ButlerImportResponse to ImportResultDto")
  void should_transform() {
    // given
    ButlerImportResponse importResponse = ButlerImportResponse.builder().totalCountRequested(666).totalCountImported(666).build();

    // when
    ImportResultDto result = underTest.transform(importResponse);

    // then
    assertThat(result.getTotalCountImported()).isEqualTo(importResponse.getTotalCountImported());
    assertThat(result.getTotalCountRequested()).isEqualTo(importResponse.getTotalCountRequested());
  }
}