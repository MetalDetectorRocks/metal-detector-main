package rocks.metaldetector.service.artist;

import org.springframework.data.domain.Pageable;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

import java.util.List;
import java.util.Optional;

public interface ArtistsService {

  Optional<ArtistDto> findArtistByDiscogsId(long discogsId);
  List<ArtistDto> findAllArtistsByDiscogsIds(long... discogsIds);
  boolean existsArtistByDiscogsId(long discogsId);

  ArtistSearchResponse searchDiscogsByName(String artistQueryString, Pageable pageable);
  ArtistSearchResponse searchSpotifyByName(String artistQueryString, Pageable pageable);

}
