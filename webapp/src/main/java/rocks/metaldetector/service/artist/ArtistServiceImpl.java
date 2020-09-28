package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistServiceImpl implements ArtistService {

  private final ArtistRepository artistRepository;
  private final ArtistTransformer artistTransformer;

  @Override
  public Optional<ArtistDto> findArtistByExternalId(String externalId, ArtistSource source) {
    return artistRepository.findByExternalIdAndSource(externalId, source)
        .map(artistTransformer::transform);
  }

  @Override
  public List<ArtistDto> findAllArtistsByExternalIds(Collection<String> externalIds) {
    List<ArtistEntity> artistEntities = artistRepository.findAllByExternalIdIn(externalIds);
    return artistEntities.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsArtistByExternalId(String externalId, ArtistSource source) {
    return artistRepository.existsByExternalIdAndSource(externalId, source);
  }

}
