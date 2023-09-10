package rocks.metaldetector.butler.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerStatisticsTransformer;
import rocks.metaldetector.butler.facade.dto.ReleaseStatisticsDto;

@Service
@AllArgsConstructor
public class ReleaseStatisticsServiceImpl implements ReleaseStatisticsService{

  private final ReleaseButlerRestClient releaseButlerRestClient;
  private final ButlerStatisticsTransformer statisticsTransformer;

  @Override
  public ReleaseStatisticsDto getReleaseStatistics() {
    ButlerStatisticsResponse response = releaseButlerRestClient.getStatistics();
    return statisticsTransformer.transform(response);
  }
}
