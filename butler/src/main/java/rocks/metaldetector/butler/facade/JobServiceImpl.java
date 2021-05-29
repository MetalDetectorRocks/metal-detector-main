package rocks.metaldetector.butler.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerImportJobTransformer;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobServiceImpl implements JobService {

  private final ReleaseButlerRestClient butlerClient;
  private final ButlerImportJobTransformer importJobResponseTransformer;

  @Override
  public void createImportJob() {
    butlerClient.createImportJob();
  }

  @Override
  public void createRetryCoverDownloadJob() {
    butlerClient.createRetryCoverDownloadJob();
  }

  @Override
  public List<ImportJobResultDto> queryImportJobResults() {
    List<ButlerImportJob> importJobResponses = butlerClient.queryImportJobResults();
    return importJobResponses.stream().map(importJobResponseTransformer::transform).collect(Collectors.toList());
  }
}
