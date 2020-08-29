package rocks.metaldetector.butler.facade;

import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;

import java.util.List;

public interface ReleaseService {

  List<ReleaseDto> findReleases(TimeRange timeRange, PageRequest pageRequest);

  List<ReleaseDto> findAllReleases(TimeRange timeRange);

  void createImportJob();

  void createRetryCoverDownloadJob();

  List<ImportJobResultDto> queryImportJobResults();
}
