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
public class ButlerJobServiceImpl implements ButlerJobService {

  private final ReleaseButlerRestClient butlerClient;
  private final ButlerImportJobTransformer importJobResponseTransformer;

  @Override
  public List<String> createImportJobs() {
    return butlerClient.createImportJobs().getImportJobIds();
  }

  @Override
  public void createRetryCoverDownloadJob() {
    butlerClient.createRetryCoverDownloadJob();
  }

  @Override
  public List<ImportJobResultDto> queryImportJobs() {
    List<ButlerImportJob> importJobResponses = butlerClient.queryImportJobs();
    return importJobResponses.stream().map(importJobResponseTransformer::transform).collect(Collectors.toList());
  }

  @Override
  public ImportJobResultDto queryImportJob(String jobId) {
    ButlerImportJob importJob = butlerClient.queryImportJob(jobId);
    return importJobResponseTransformer.transform(importJob);
  }
}
