package rocks.metaldetector.butler.facade;

import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.time.LocalDate;
import java.util.List;

public interface ReleaseService {

  List<ReleaseDto> findAllReleases(Iterable<String> artists, LocalDate dateFrom, LocalDate dateTo);

  void createImportJob();

  void createRetryCoverDownloadJob();

  List<ImportJobResultDto> queryImportJobResults();
}
