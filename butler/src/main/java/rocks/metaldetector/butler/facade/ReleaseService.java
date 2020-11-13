package rocks.metaldetector.butler.facade;

import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.TimeRange;

import java.util.List;

public interface ReleaseService {

  Page<ReleaseDto> findReleases(Iterable<String> artists, TimeRange timeRange, PageRequest pageRequest);

  List<ReleaseDto> findAllReleases(Iterable<String> artists, TimeRange timeRange);

  void createImportJob();

  void createRetryCoverDownloadJob();

  List<ImportJobResultDto> queryImportJobResults();

  void updateReleaseState(long releaseId, String state);
}
