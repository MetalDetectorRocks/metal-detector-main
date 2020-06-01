package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.web.api.response.DetectorImportJobResponse;

class DetectorImportJobResponseTransformerTest implements WithAssertions {

  private final DetectorImportJobResponseTransformer underTest = new DetectorImportJobResponseTransformer();

  @Test
  @DisplayName("Import job result should be transformed correctly")
  void test_result_is_transformed() {
    // given
    ImportJobResultDto importJobResultDto = ImportJobResultDto.builder().totalCountImported(666).totalCountRequested(666).build();

    // when
    DetectorImportJobResponse response = underTest.transform(importJobResultDto);

    // then
    assertThat(response.getTotalCountImported()).isEqualTo(importJobResultDto.getTotalCountImported());
    assertThat(response.getTotalCountRequested()).isEqualTo(importJobResultDto.getTotalCountRequested());
  }
}