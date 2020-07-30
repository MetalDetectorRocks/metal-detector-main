package rocks.metaldetector.butler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Profile("mockmode")
@AllArgsConstructor
@Slf4j
public class ReleaseButlerRestClientMock implements ReleaseButlerRestClient {

  private final ResourceLoader resourceLoader;
  private final ObjectMapper objectMapper;

  @Override
  public ButlerReleasesResponse queryReleases(ButlerReleasesRequest request) {
    Resource mockResource = resourceLoader.getResource("classpath:mock-releases.json");
    try (Reader reader = new InputStreamReader(mockResource.getInputStream(), UTF_8)) {
      return objectMapper.readValue(reader, ButlerReleasesResponse.class);
    }
    catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  @Override
  public void createImportJob() {
    log.info("Import job successfully created!");
  }

  @Override
  public void createRetryCoverDownloadJob() {
    log.info("Covers successfully downloaded!");
  }

  @Override
  public List<ButlerImportJob> queryImportJobResults() {
    return List.of(
        new ButlerImportJob(650, 630, LocalDateTime.of(2020, 7, 28, 13, 37, 16), LocalDateTime.of(2020, 7, 28, 13, 39, 51), "Metal Achives"),
        new ButlerImportJob(640, 110, LocalDateTime.of(2020, 7, 21, 15, 1, 13), LocalDateTime.of(2020, 7, 21, 15, 4, 45), "Metal Achives"),
        new ButlerImportJob(645, 90, LocalDateTime.of(2020, 7, 14, 9, 16, 24), LocalDateTime.of(2020, 7, 14, 9, 19, 27), "Metal Achives"),
        new ButlerImportJob(684, 105, LocalDateTime.of(2020, 7, 7, 21, 14, 6), LocalDateTime.of(2020, 7, 7, 21, 19, 46), "Metal Achives")
    );
  }
}
