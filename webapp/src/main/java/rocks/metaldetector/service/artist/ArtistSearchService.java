package rocks.metaldetector.service.artist;

import org.springframework.data.domain.Pageable;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;

public interface ArtistSearchService {

    ArtistSearchResponse searchDiscogsByName(String artistQueryString, Pageable pageable);
    ArtistSearchResponse searchSpotifyByName(String artistQueryString, Pageable pageable);
}
