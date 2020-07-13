package rocks.metaldetector.spotify.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.client.SpotifyArtistSearchClient;
import rocks.metaldetector.spotify.client.SpotifyAuthenticationClient;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistSearchResultTransformer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

@Service
@AllArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

  private final SpotifyArtistSearchClient spotifyArtistSearchClient;
  private final SpotifyAuthenticationClient spotifyAuthenticationClient;
  private final SpotifyArtistSearchResultTransformer searchResultTransformer;

  @Override
  public SpotifyArtistSearchResultDto searchArtists(String artistQueryString, int pageNumber, int pageSize) {
    String authenticationToken = spotifyAuthenticationClient.getAuthenticationToken();
    SpotifyArtistSearchResultContainer searchResult = spotifyArtistSearchClient.searchByName(authenticationToken, artistQueryString, pageNumber, pageSize);
    return searchResultTransformer.transform(searchResult);
  }
}
