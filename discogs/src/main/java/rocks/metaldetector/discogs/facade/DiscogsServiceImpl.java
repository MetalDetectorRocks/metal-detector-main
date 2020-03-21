package rocks.metaldetector.discogs.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.client.DiscogsArtistSearchRestClient;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;

import java.util.Optional;

@Service
public class DiscogsServiceImpl implements DiscogsService {

  private final DiscogsArtistSearchRestClient searchClient;

  @Autowired
  public DiscogsServiceImpl(DiscogsArtistSearchRestClient searchClient) {
    this.searchClient = searchClient;
  }

  @Override
  public DiscogsArtistSearchResultDto searchArtistByName(String artistQueryString, int pageNumber, int pageSize) {
    Optional<DiscogsArtistSearchResultDto> result = searchClient.searchByName(artistQueryString, pageNumber, pageSize);
    return result.orElseThrow(DiscogsArtistNotFoundException::new);
  }

  @Override
  public DiscogsArtistDto searchArtistById(long artistId) {
    Optional<DiscogsArtistDto> result = searchClient.searchById(artistId);
    return result.orElseThrow(DiscogsArtistNotFoundException::new);
  }
}
