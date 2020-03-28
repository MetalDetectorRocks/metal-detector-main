package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;

import java.time.LocalDate;

@Service
public class ButlerReleaseRequestTransformer {

  public ButlerReleasesRequest transform(Iterable<String> artists, LocalDate from, LocalDate to) {
    return ButlerReleasesRequest.builder()
            .artists(artists)
            .dateFrom(from)
            .dateTo(to)
            .build();
  }
}
