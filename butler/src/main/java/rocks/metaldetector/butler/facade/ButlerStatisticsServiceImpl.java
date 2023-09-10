package rocks.metaldetector.butler.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerStatisticsTransformer;
import rocks.metaldetector.butler.facade.dto.ButlerStatisticsDto;

@Service
@AllArgsConstructor
public class ButlerStatisticsServiceImpl implements ButlerStatisticsService {

  private final ReleaseButlerRestClient releaseButlerRestClient;
  private final ButlerStatisticsTransformer statisticsTransformer;

  @Override
  public ButlerStatisticsDto getButlerStatistics() {
    ButlerStatisticsResponse response = releaseButlerRestClient.getStatistics();
    return statisticsTransformer.transform(response);
  }
}
