package rocks.metaldetector.service.summary;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistTransformer;

import java.util.List;
import java.util.stream.Collectors;

import static rocks.metaldetector.service.summary.SummaryServiceImpl.RESULT_LIMIT;

@Component
@AllArgsConstructor
public class ArtistCollector {

  private final ArtistRepository artistRepository;
  private final ArtistTransformer artistTransformer;

  public List<ArtistDto> collectTopFollowedArtists() {
    return artistRepository.findTopArtists(RESULT_LIMIT).stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }
}
