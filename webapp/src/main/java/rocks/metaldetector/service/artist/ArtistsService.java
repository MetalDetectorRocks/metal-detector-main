package rocks.metaldetector.service.artist;

import org.springframework.data.domain.Pageable;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArtistsService {

  Optional<ArtistDto> findArtistByExternalId(String externalId);
  List<ArtistDto> findAllArtistsByExternalIds(Collection<String> externalIds);
  boolean existsArtistByExternalId(String externalId);

  ArtistSearchResponse searchDiscogsByName(String artistQueryString, Pageable pageable);
  ArtistSearchResponse searchSpotifyByName(String artistQueryString, Pageable pageable);

}
