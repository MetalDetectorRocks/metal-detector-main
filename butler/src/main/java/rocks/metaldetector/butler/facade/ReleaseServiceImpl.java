package rocks.metaldetector.butler.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseRequestTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerSortTransformer;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;

import java.util.List;

@Service
@AllArgsConstructor
public class ReleaseServiceImpl implements ReleaseService {

  private final ReleaseButlerRestClient butlerClient;
  private final ButlerReleaseRequestTransformer queryRequestTransformer;
  private final ButlerSortTransformer sortTransformer;
  private final ButlerReleaseResponseTransformer queryResponseTransformer;

  @Override
  public List<ReleaseDto> findAllReleases(Iterable<String> artists, TimeRange timeRange) {
    ButlerReleasesRequest request = queryRequestTransformer.transform(artists, timeRange, null, null);
    ButlerReleasesResponse response = butlerClient.queryAllReleases(request);
    return queryResponseTransformer.transformToList(response);
  }

  @Override
  public Page<ReleaseDto> findReleases(Iterable<String> artists, TimeRange timeRange, String query, PageRequest pageRequest) {
    ButlerReleasesRequest request = queryRequestTransformer.transform(artists, timeRange, query, pageRequest);
    String sortString = sortTransformer.transform(pageRequest.getSort());
    ButlerReleasesResponse response = butlerClient.queryReleases(request, sortString);
    return queryResponseTransformer.transformToPage(response);
  }

  @Override
  public void updateReleaseState(long releaseId, String state) {
    butlerClient.updateReleaseState(releaseId, state);
  }
}
