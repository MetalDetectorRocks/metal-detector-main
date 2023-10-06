package rocks.metaldetector.service.imports;

import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.util.List;

public interface JobService {

  void createImportJobs();

  void createRetryCoverDownloadJob();

  List<ImportJobResultDto> queryImportJobs();
}
