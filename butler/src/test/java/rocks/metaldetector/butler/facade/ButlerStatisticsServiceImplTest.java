package rocks.metaldetector.butler.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerStatisticsTransformer;
import rocks.metaldetector.butler.facade.dto.ButlerStatisticsDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ButlerStatisticsServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseButlerRestClient releaseButlerRestClient;

  @Mock
  private ButlerStatisticsTransformer statisticsTransformer;

  @InjectMocks
  private ButlerStatisticsServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(releaseButlerRestClient, statisticsTransformer);
  }

  @Test
  @DisplayName("releaseButlerRestClient is called for statistics")
  void test_rest_client_called() {
    // when
    underTest.getButlerStatistics();

    // then
    verify(releaseButlerRestClient).getStatistics();
  }

  @Test
  @DisplayName("statisticsTransformer is called for statistics")
  void test_statistics_trafo_called() {
    // given
    var expectedStatistics = ButlerStatisticsResponse.builder().build();
    doReturn(expectedStatistics).when(releaseButlerRestClient).getStatistics();

    // when
    underTest.getButlerStatistics();

    // then
    verify(statisticsTransformer).transform(expectedStatistics);
  }

  @Test
  @DisplayName("statistics are returned")
  void test_statistics_returned() {
    // given
    var expectedStatistics = ButlerStatisticsDto.builder().build();
    doReturn(expectedStatistics).when(statisticsTransformer).transform(any());

    // when
    var result = underTest.getButlerStatistics();

    // then
    assertThat(result).isEqualTo(expectedStatistics);
  }
}
