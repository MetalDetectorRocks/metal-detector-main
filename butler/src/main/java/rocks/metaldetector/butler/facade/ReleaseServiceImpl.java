package rocks.metaldetector.butler.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerImportJobResponseTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseRequestTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ReleaseServiceImpl implements ReleaseService {

  private final ReleaseButlerRestClient butlerClient;
  private final ButlerReleaseRequestTransformer queryRequestTransformer;
  private final ButlerReleaseResponseTransformer queryResponseTransformer;
  private final ButlerImportJobResponseTransformer importResponseTransformer;

  @Override
  public List<ReleaseDto> findReleases(Iterable<String> artists, LocalDate dateFrom, LocalDate dateTo) {
    ButlerReleasesRequest request = queryRequestTransformer.transform(artists, dateFrom, dateTo);
    ButlerReleasesResponse response = butlerClient.queryReleases(request);
    return queryResponseTransformer.transform(response);
  }

  @Override
  public ImportJobResultDto createImportJob() {
    ButlerImportJobResponse response = butlerClient.createImportJob();
    return importResponseTransformer.transform(response);
  }
}
