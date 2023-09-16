package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.butler.api.ButlerImportInfo;
import rocks.metaldetector.butler.api.ButlerReleaseInfo;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.facade.dto.ButlerStatisticsDto;
import rocks.metaldetector.butler.facade.dto.ImportInfoDto;
import rocks.metaldetector.butler.facade.dto.ReleaseInfoDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ButlerStatisticsTransformer {

  public ButlerStatisticsDto transform(ButlerStatisticsResponse response) {
    ReleaseInfoDto releaseInfo = transformReleaseInfo(response.getReleaseInfo());
    List<ImportInfoDto> importInfo = transformImportInfo(response.getImportInfo());
    return ButlerStatisticsDto.builder()
        .releaseInfo(releaseInfo)
        .importInfo(importInfo)
        .build();
  }

  private ReleaseInfoDto transformReleaseInfo(ButlerReleaseInfo releaseInfo){
    if (releaseInfo == null) {
      return ReleaseInfoDto.builder().releasesPerMonth(Collections.emptyMap()).build();
    }
    return ReleaseInfoDto.builder()
        .totalReleases(releaseInfo.getTotalReleases())
        .upcomingReleases(releaseInfo.getUpcomingReleases())
        .releasesPerMonth(releaseInfo.getReleasesPerMonth())
        .releasesThisMonth(releaseInfo.getReleasesThisMonth())
        .duplicates(releaseInfo.getDuplicates())
        .build();
  }

  private List<ImportInfoDto> transformImportInfo(List<ButlerImportInfo> importInfo) {
    if (importInfo == null) {
      return Collections.emptyList();
    }
    return importInfo.stream()
        .map((butlerImportInfo ->
                 ImportInfoDto.builder()
                     .source(butlerImportInfo.getSource())
                     .successRate(butlerImportInfo.getSuccessRate())
                     .lastImport(butlerImportInfo.getLastImport())
                     .lastSuccessfulImport(butlerImportInfo.getLastSuccessfulImport())
                     .build()))
        .collect(Collectors.toList());
  }
}
