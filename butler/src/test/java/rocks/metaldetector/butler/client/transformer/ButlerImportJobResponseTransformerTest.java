package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerImportJobResponseFactory;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.time.LocalDateTime;
import java.util.stream.Stream;

class ButlerImportJobResponseTransformerTest implements WithAssertions {

  private final ButlerImportJobResponseTransformer underTest = new ButlerImportJobResponseTransformer();

  @Test
  @DisplayName("Should transform ButlerImportJobResponse to ImportJobResultDto")
  void should_transform() {
    // given
    ButlerImportJobResponse importJobResponse = ButlerImportJobResponseFactory.createDefault();

    // when
    ImportJobResultDto result = underTest.transform(importJobResponse);

    // then
    assertThat(result.getTotalCountImported()).isEqualTo(importJobResponse.getTotalCountImported());
    assertThat(result.getTotalCountRequested()).isEqualTo(importJobResponse.getTotalCountRequested());
    assertThat(result.getStartTime()).isEqualTo(importJobResponse.getStartTime());
    assertThat(result.getEndTime()).isEqualTo(importJobResponse.getEndTime());
  }

  @ParameterizedTest(name = "'finished' is <{1}> when 'endTime' is <{0}>")
  @MethodSource("finishTestDataProvider")
  @DisplayName("'finished' is set depending on 'endTime'")
  void should_transform_finish(LocalDateTime endTime, boolean expectedResult) {
    // given
    ButlerImportJobResponse importJobResponse = ButlerImportJobResponseFactory.createDefault();
    importJobResponse.setEndTime(endTime);

    // when
    ImportJobResultDto result = underTest.transform(importJobResponse);

    // then
    assertThat(result.isFinished()).isEqualTo(expectedResult);
  }

  private static Stream<Arguments> finishTestDataProvider() {
    return Stream.of(
            Arguments.of(LocalDateTime.now(), true),
            Arguments.of(null, false)
    );
  }

  @ParameterizedTest(name = "'duration' is <{2}> when 'startTime' is <{0}> and 'endTime' is <{1}>")
  @MethodSource("durationTestDataProvider")
  @DisplayName("'duration' is set only if 'startTime' and 'endTime' is set")
  void should_transform_duration(LocalDateTime startTime, LocalDateTime endTime, long expectedResult) {
    // given
    ButlerImportJobResponse importJobResponse = ButlerImportJobResponseFactory.createDefault();
    importJobResponse.setStartTime(startTime);
    importJobResponse.setEndTime(endTime);

    // when
    ImportJobResultDto result = underTest.transform(importJobResponse);

    // then
    assertThat(result.getDurationInSeconds()).isEqualTo(expectedResult);
  }

  private static Stream<Arguments> durationTestDataProvider() {
    return Stream.of(
            Arguments.of(LocalDateTime.of(2020,7, 1, 12, 0, 0), LocalDateTime.of(2020,7, 1, 12, 1, 0), 60),
            Arguments.of(LocalDateTime.now(), null, 0),
            Arguments.of(null, LocalDateTime.now(), 0),
            Arguments.of(null, null, 0)
    );
  }
}
