package rocks.metaldetector.butler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
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
    // do nothing
  }

  @Override
  public List<ButlerImportJobResponse> queryImportJobResults() {
    return List.of(
            new ButlerImportJobResponse(650, 630, LocalDateTime.of(2020, 7, 28, 13, 37, 16), LocalDateTime.of(2020, 7, 28, 13, 39, 51)),
            new ButlerImportJobResponse(640, 110, LocalDateTime.of(2020, 7, 21, 15, 1, 13), LocalDateTime.of(2020, 7, 21, 15, 4, 45)),
            new ButlerImportJobResponse(645, 90, LocalDateTime.of(2020, 7, 14, 9, 16, 24), LocalDateTime.of(2020, 7, 14, 9, 19, 27)),
            new ButlerImportJobResponse(684, 105, LocalDateTime.of(2020, 7, 7, 21, 14, 6), LocalDateTime.of(2020, 7, 7, 21, 19, 46))
    );
  }
}
