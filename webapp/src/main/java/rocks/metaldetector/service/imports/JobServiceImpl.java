package rocks.metaldetector.service.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.ButlerJobService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.config.misc.DelayedEventPublisher;

import java.util.List;

@Service
@AllArgsConstructor
public class JobServiceImpl implements JobService {

  private final ButlerJobService butlerJobService;
  private final DelayedEventPublisher delayedEventPublisher;

  @Override
  public void createImportJobs() {
    List<String> jobsIds = butlerJobService.createImportJobs();
    jobsIds.forEach(delayedEventPublisher::publishDelayedJobEvent);
  }

  @Override
  public void createRetryCoverDownloadJob() {
    butlerJobService.createRetryCoverDownloadJob();
  }

  @Override
  public List<ImportJobResultDto> queryImportJobs() {
    return butlerJobService.queryImportJobs();
  }
}
