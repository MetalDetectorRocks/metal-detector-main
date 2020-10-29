package rocks.metaldetector.spotify.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.authorization.SpotifyUserAuthorizationResponse;
import rocks.metaldetector.spotify.api.imports.SpotfiyAlbumImportResult;
import rocks.metaldetector.spotify.api.imports.SpotifyAlbumImportResultItem;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;
import rocks.metaldetector.spotify.client.SpotifyArtistSearchClient;
import rocks.metaldetector.spotify.client.SpotifyAuthorizationClient;
import rocks.metaldetector.spotify.client.SpotifyImportClient;
import rocks.metaldetector.spotify.client.transformer.SpotifyAlbumTransformer;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistSearchResultTransformer;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistTransformer;
import rocks.metaldetector.spotify.client.transformer.SpotifyUserAuthorizationTransformer;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.spotify.facade.dto.SpotifyAlbumDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyUserAuthorizationDto;
import rocks.metaldetector.support.Endpoints;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

  private static final String USER_AUTHORIZATION_ENDPOINT = "/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s&state=";
  static final List<String> REQUIRED_SCOPES = List.of("user-library-read", "user-follow-read");

  private final SpotifyArtistSearchClient artistSearchClient;
  private final SpotifyAuthorizationClient authorizationClient;
  private final SpotifyImportClient importClient;
  private final SpotifyArtistSearchResultTransformer searchResultTransformer;
  private final SpotifyArtistTransformer artistTransformer;
  private final SpotifyUserAuthorizationTransformer userAuthorizationTransformer;
  private final SpotifyAlbumTransformer albumTransformer;
  private final SpotifyProperties spotifyProperties;

  @Override
  public SpotifyArtistSearchResultDto searchArtistByName(String artistQueryString, int pageNumber, int pageSize) {
    String authorizationToken = authorizationClient.getAppAuthorizationToken();
    SpotifyArtistSearchResultContainer searchResult = artistSearchClient.searchByName(authorizationToken, artistQueryString, pageNumber, pageSize);
    return searchResultTransformer.transform(searchResult);
  }

  @Override
  public SpotifyArtistDto searchArtistById(String artistId) {
    String authenticationToken = authorizationClient.getAppAuthorizationToken();
    SpotifyArtist spotifyArtist = artistSearchClient.searchById(authenticationToken, artistId);
    return artistTransformer.transform(spotifyArtist);
  }

  @Override
  public List<SpotifyArtistDto> searchArtistsByIds(List<String> artistIds) {
    String authenticationToken = authorizationClient.getAppAuthorizationToken();
    SpotifyArtistsContainer spotifyArtistsContainer = artistSearchClient.searchByIds(authenticationToken, artistIds);
    return spotifyArtistsContainer.getArtists().stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public String getSpotifyAuthorizationUrl() {
    String endpoint = String.format(USER_AUTHORIZATION_ENDPOINT,
                                    spotifyProperties.getClientId(),
                                    spotifyProperties.getApplicationHostUrl() + Endpoints.Frontend.PROFILE + Endpoints.Frontend.SPOTIFY_CALLBACK,
                                    URLEncoder.encode(String.join(" ", REQUIRED_SCOPES), Charset.defaultCharset()));
    return spotifyProperties.getAuthenticationBaseUrl() + endpoint;
  }

  @Override
  public SpotifyUserAuthorizationDto getAccessToken(String code) {
    SpotifyUserAuthorizationResponse response = authorizationClient.getUserAuthorizationToken(code);
    return userAuthorizationTransformer.transform(response);
  }

  @Override
  public List<SpotifyAlbumDto> importAlbums(String token) {
    int offset = 0;
    List<SpotifyAlbumImportResultItem> resultItems = new ArrayList<>();
    SpotfiyAlbumImportResult importResult;
    do {
      importResult = importClient.importAlbums(token, offset);
      resultItems.addAll(importResult.getItems());
      offset += importResult.getLimit();
    }
    while (offset < importResult.getTotal());

    return resultItems.stream()
        .map(SpotifyAlbumImportResultItem::getAlbum)
        .map(albumTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public SpotifyUserAuthorizationDto refreshToken(String refreshToken) {
    SpotifyUserAuthorizationResponse response = authorizationClient.refreshUserAuthorizationToken(refreshToken);
    return userAuthorizationTransformer.transform(response);
  }
}
