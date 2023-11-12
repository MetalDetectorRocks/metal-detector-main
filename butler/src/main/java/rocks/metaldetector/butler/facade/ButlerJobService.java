package rocks.metaldetector.butler.facade;

import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.util.List;

public interface ButlerJobService {

  List<String> createImportJobs();

  void createRetryCoverDownloadJob();

  List<ImportJobResultDto> queryImportJobs();

  ImportJobResultDto queryImportJob(String jobId);
}
