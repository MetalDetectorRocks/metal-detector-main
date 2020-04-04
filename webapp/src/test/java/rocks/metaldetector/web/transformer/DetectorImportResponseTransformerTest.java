package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.facade.dto.ImportResultDto;
import rocks.metaldetector.web.api.response.DetectorImportResponse;

class DetectorImportResponseTransformerTest implements WithAssertions {

  private DetectorImportResponseTransformer underTest = new DetectorImportResponseTransformer();

  @Test
  @DisplayName("ImportResult should be transformed correctly")
  void test_result_is_transformed() {
    // given
    ImportResultDto importResultDto = ImportResultDto.builder().totalCountImported(666).totalCountRequested(66).build();

    // when
    DetectorImportResponse response = underTest.transform(importResultDto);

    // then
    assertThat(response.getTotalCountImported()).isEqualTo(importResultDto.getTotalCountImported());
    assertThat(response.getTotalCountRequested()).isEqualTo(importResultDto.getTotalCountRequested());
  }
}