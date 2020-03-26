package rocks.metaldetector.butler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Profile("mockmode")
public class ReleaseButlerRestClientMock implements ReleaseButlerRestClient {

  private final ResourceLoader resourceLoader;
  private final ObjectMapper objectMapper;

  @Autowired
  public ReleaseButlerRestClientMock(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
    this.resourceLoader = resourceLoader;
    this.objectMapper = objectMapper;
  }

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
}
