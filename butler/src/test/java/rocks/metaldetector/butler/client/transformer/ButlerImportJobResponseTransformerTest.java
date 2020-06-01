package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.ButlerDtoFactory;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

class ButlerImportJobResponseTransformerTest implements WithAssertions {

  private final ButlerImportJobResponseTransformer underTest = new ButlerImportJobResponseTransformer();

  @Test
  @DisplayName("Should transform ButlerImportJobResponse to ImportJobResultDto")
  void should_transform() {
    // given
    ButlerImportJobResponse importJobResponse = ButlerDtoFactory.ButlerImportJobResponseFactory.createDefault();

    // when
    ImportJobResultDto result = underTest.transform(importJobResponse);

    // then
    assertThat(result.getTotalCountImported()).isEqualTo(importJobResponse.getTotalCountImported());
    assertThat(result.getTotalCountRequested()).isEqualTo(importJobResponse.getTotalCountRequested());
  }
}