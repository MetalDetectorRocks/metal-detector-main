package rocks.metaldetector.butler.facade;

import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.util.List;

public interface JobService {

  void createImportJob();

  void createRetryCoverDownloadJob();

  List<ImportJobResultDto> queryImportJobResults();
}
