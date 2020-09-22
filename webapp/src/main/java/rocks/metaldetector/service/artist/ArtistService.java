package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistSource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArtistService {

  Optional<ArtistDto> findArtistByExternalId(String externalId, ArtistSource source);
  List<ArtistDto> findAllArtistsByExternalIds(Collection<String> externalIds);
  boolean existsArtistByExternalId(String externalId, ArtistSource source);

}
