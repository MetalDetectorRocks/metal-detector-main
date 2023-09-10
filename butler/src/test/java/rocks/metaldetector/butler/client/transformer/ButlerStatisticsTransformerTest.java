package rocks.metaldetector.butler.client.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.api.ButlerReleaseInfo;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.facade.dto.ButlerStatisticsDto;
import rocks.metaldetector.butler.facade.dto.ReleaseInfoDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ButlerStatisticsTransformerTest implements WithAssertions {

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private ButlerStatisticsTransformer underTest;

  @AfterEach
  void tearDown() {
    reset(objectMapper);
  }

  @Test
  @DisplayName("Should call objectMapper")
  void should_call_object_mapper() {
    // given
    var releaseInfo = ButlerReleaseInfo.builder().build();
    var butlerResponse = ButlerStatisticsResponse.builder().releaseInfo(releaseInfo).build();

    // when
    underTest.transform(butlerResponse);

    // then
    verify(objectMapper).convertValue(butlerResponse, ButlerStatisticsDto.class);
  }

  @Test
  @DisplayName("Should return Dto")
  void should_return_dto() {
    // given
    var releaseInfo = ReleaseInfoDto.builder().build();
    var butlerStatistics = ButlerStatisticsDto.builder().releaseInfo(releaseInfo).build();
    doReturn(butlerStatistics).when(objectMapper).convertValue(any(), eq(ButlerStatisticsDto.class));

    // when
    ButlerStatisticsDto result = underTest.transform(ButlerStatisticsResponse.builder().build());

    // then
    assertThat(result).isEqualTo(butlerStatistics);
  }
}
