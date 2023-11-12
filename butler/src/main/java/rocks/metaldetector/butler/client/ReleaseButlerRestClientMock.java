package rocks.metaldetector.butler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportCreatedResponse;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerReleaseInfo;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Profile("mockmode")
@AllArgsConstructor
@Slf4j
public class ReleaseButlerRestClientMock implements ReleaseButlerRestClient {

  private final ResourceLoader resourceLoader;
  private final ObjectMapper objectMapper;

  @Override
  public ButlerReleasesResponse queryAllReleases(ButlerReleasesRequest request) {
    return loadReleasesFromFile();
  }

  @Override
  public ButlerReleasesResponse queryReleases(ButlerReleasesRequest request, String sort) {
    return loadReleasesFromFile();
  }

  private ButlerReleasesResponse loadReleasesFromFile() {
    Resource mockResource = resourceLoader.getResource("classpath:mock-releases.json");
    try (Reader reader = new InputStreamReader(mockResource.getInputStream(), UTF_8)) {
      return objectMapper.readValue(reader, ButlerReleasesResponse.class);
    }
    catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  @Override
  public ButlerImportCreatedResponse createImportJobs() {
    return ButlerImportCreatedResponse.builder()
        .importJobIds(List.of(UUID.randomUUID().toString()))
        .build();
  }

  @Override
  public void createRetryCoverDownloadJob() {
    log.info("Covers successfully downloaded!");
  }

  @Override
  public List<ButlerImportJob> queryImportJobs() {
    return List.of(
        new ButlerImportJob(650, 630, LocalDateTime.of(2020, 7, 28, 13, 37, 16), LocalDateTime.of(2020, 7, 28, 13, 39, 51), "Successful", "Metal Achives"),
        new ButlerImportJob(640, 110, LocalDateTime.of(2020, 7, 21, 15, 1, 13), LocalDateTime.of(2020, 7, 21, 15, 4, 45), "Successful", "Metal Achives"),
        new ButlerImportJob(645, 90, LocalDateTime.of(2020, 7, 14, 9, 16, 24), LocalDateTime.of(2020, 7, 14, 9, 19, 27), "Successful", "Metal Achives"),
        new ButlerImportJob(684, 105, LocalDateTime.of(2020, 7, 7, 21, 14, 6), LocalDateTime.of(2020, 7, 7, 21, 19, 46), "Successful", "Metal Achives")
    );
  }

  @Override
  public ButlerImportJob queryImportJob(String jobId) {
    return  new ButlerImportJob(650, 630, LocalDateTime.of(2020, 7, 28, 13, 37, 16), LocalDateTime.of(2020, 7, 28, 13, 39, 51), "Successful", "Metal Achives");
  }

  @Override
  public void updateReleaseState(long releaseId, String state) {
    log.info("Release state successfully updated!");
  }

  @Override
  public ButlerStatisticsResponse getStatistics() {
    return ButlerStatisticsResponse.builder()
        .releaseInfo(ButlerReleaseInfo.builder()
                         .totalReleases(666)
                         .duplicates(6)
                         .releasesPerMonth(Map.of(YearMonth.of(2020, 1), 2000,
                                                  YearMonth.of(2020, 2), 3000,
                                                  YearMonth.of(2020, 3), 6400,
                                                  YearMonth.of(2020, 4), 3400,
                                                  YearMonth.of(2020, 5), 4500,
                                                  YearMonth.of(2020, 6), 6666))
                         .releasesThisMonth(66)
                         .upcomingReleases(67)
                         .build())
        .build();
  }
}
