package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static rocks.metaldetector.butler.ButlerDtoFactory.ButlerImportJobFactory;

@ExtendWith(MockitoExtension.class)
class ButlerImportJobTransformerTest implements WithAssertions {

  @InjectMocks
  private ButlerImportJobTransformer underTest;

  @Test
  @DisplayName("Should transform ButlerImportJobResponse to ImportJobResultDto")
  void should_transform() {
    // given
    ButlerImportJob importJob = ButlerImportJobFactory.createDefault();

    // when
    ImportJobResultDto result = underTest.transform(importJob);

    // then
    assertThat(result.getTotalCountImported()).isEqualTo(importJob.getTotalCountImported());
    assertThat(result.getTotalCountRequested()).isEqualTo(importJob.getTotalCountRequested());
    assertThat(result.getStartTime()).isEqualTo(importJob.getStartTime());
    assertThat(result.getEndTime()).isEqualTo(importJob.getEndTime());
    assertThat(result.getState()).isEqualTo(importJob.getState());
    assertThat(result.getSource()).isEqualTo(importJob.getSource());
  }

  @ParameterizedTest(name = "'duration' is <{2}> when 'startTime' is <{0}> and 'endTime' is <{1}>")
  @MethodSource("durationTestDataProvider")
  @DisplayName("'duration' is set only if 'startTime' and 'endTime' is set")
  void should_transform_duration(LocalDateTime startTime, LocalDateTime endTime, long expectedResult) {
    // given
    ButlerImportJob importJob = ButlerImportJobFactory.createDefault();
    importJob.setStartTime(startTime);
    importJob.setEndTime(endTime);

    // when
    ImportJobResultDto result = underTest.transform(importJob);

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
