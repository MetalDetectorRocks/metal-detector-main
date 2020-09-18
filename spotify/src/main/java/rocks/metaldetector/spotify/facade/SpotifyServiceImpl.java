package rocks.metaldetector.spotify.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.search.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.client.SpotifyArtistSearchClient;
import rocks.metaldetector.spotify.client.SpotifyAuthenticationClient;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistSearchResultTransformer;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistTransformer;
import rocks.metaldetector.spotify.config.SpotifyConfig;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.support.Endpoints;

import java.net.URLEncoder;
import java.nio.charset.Charset;

@Service
@AllArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

  private static final String USER_AUTHORIZATION_ENDPOINT = "/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s&state=";
  static final String REQUIRED_SCOPES = "user-library-read user-follow-read";

  private final SpotifyArtistSearchClient spotifyArtistSearchClient;
  private final SpotifyAuthenticationClient spotifyAuthenticationClient;
  private final SpotifyArtistSearchResultTransformer searchResultTransformer;
  private final SpotifyArtistTransformer spotifyArtistTransformer;
  private final SpotifyConfig spotifyConfig;

  @Override
  public SpotifyArtistSearchResultDto searchArtistByName(String artistQueryString, int pageNumber, int pageSize) {
    String authenticationToken = spotifyAuthenticationClient.getAuthenticationToken();
    SpotifyArtistSearchResultContainer searchResult = spotifyArtistSearchClient.searchByName(authenticationToken, artistQueryString, pageNumber, pageSize);
    return searchResultTransformer.transform(searchResult);
  }

  @Override
  public SpotifyArtistDto searchArtistById(String artistId) {
    String authenticationToken = spotifyAuthenticationClient.getAuthenticationToken();
    SpotifyArtist spotifyArtist = spotifyArtistSearchClient.searchById(authenticationToken, artistId);
    return spotifyArtistTransformer.transform(spotifyArtist);
  }

  @Override
  public String getSpotifyAuthorizationUrl() {
    String endpoint = String.format(USER_AUTHORIZATION_ENDPOINT,
                                    spotifyConfig.getClientId(),
                                    URLEncoder.encode(spotifyConfig.getHost() + ":" + spotifyConfig.getPort() + Endpoints.Frontend.PROFILE + Endpoints.Frontend.AUTHORIZE, Charset.defaultCharset()),
                                    URLEncoder.encode(REQUIRED_SCOPES, Charset.defaultCharset()));
    return spotifyConfig.getAuthenticationBaseUrl() + endpoint;
  }
}
