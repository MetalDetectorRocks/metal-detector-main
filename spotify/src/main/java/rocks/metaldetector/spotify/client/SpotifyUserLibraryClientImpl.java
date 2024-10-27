package rocks.metaldetector.spotify.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.spotify.api.imports.SpotifyFollowedArtistsPage;
import rocks.metaldetector.spotify.api.imports.SpotifyFollowedArtistsPageContainer;
import rocks.metaldetector.spotify.api.imports.SpotifySavedAlbumsPage;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
@Profile({"default", "preview", "prod"})
public class SpotifyUserLibraryClientImpl implements SpotifyUserLibraryClient {

  static final int LIMIT = 50;
  static final String OFFSET_PARAMETER_NAME = "offset";
  static final String LIMIT_PARAMETER_NAME = "limit";
  static final String FOLLOWED_ARTISTS_FIRST_PAGE_ENDPOINT = "/v1/me/following?type=artist&limit={" + LIMIT_PARAMETER_NAME + "}";
  static final String MY_ALBUMS_ENDPOINT = "/v1/me/albums?limit={" + LIMIT_PARAMETER_NAME + "}&" +
                                               "offset={" + OFFSET_PARAMETER_NAME + "}";

  private final RestOperations spotifyOAuthAuthorizationCodeRestTemplate;
  private final SpotifyProperties spotifyProperties;

  public SpotifyUserLibraryClientImpl(@Qualifier("spotifyOAuthAuthorizationCodeRestTemplate") RestOperations spotifyOAuthAuthorizationCodeRestTemplate,
                                      SpotifyProperties spotifyProperties) {
    this.spotifyOAuthAuthorizationCodeRestTemplate = spotifyOAuthAuthorizationCodeRestTemplate;
    this.spotifyProperties = spotifyProperties;
  }

  @Override
  public SpotifySavedAlbumsPage fetchLikedAlbums(int offset) {
    HttpEntity<Object> httpEntity = new HttpEntity<>(null);
    ResponseEntity<SpotifySavedAlbumsPage> responseEntity = spotifyOAuthAuthorizationCodeRestTemplate.exchange(
        spotifyProperties.getRestBaseUrl() + MY_ALBUMS_ENDPOINT,
        GET,
        httpEntity,
        SpotifySavedAlbumsPage.class,
        Map.of(OFFSET_PARAMETER_NAME, offset,
               LIMIT_PARAMETER_NAME, LIMIT)
    );

    SpotifySavedAlbumsPage result = responseEntity.getBody();
    var shouldNotHappen = result == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get albums from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }
    return result;
  }

  @Override
  public SpotifyFollowedArtistsPage fetchFollowedArtists(String nextPage) {
    var endpoint = nextPage == null ? spotifyProperties.getRestBaseUrl() + FOLLOWED_ARTISTS_FIRST_PAGE_ENDPOINT : nextPage;
    HttpEntity<Object> httpEntity = new HttpEntity<>(null);
    ResponseEntity<SpotifyFollowedArtistsPageContainer> responseEntity = spotifyOAuthAuthorizationCodeRestTemplate.exchange(
        endpoint,
        GET,
        httpEntity,
        SpotifyFollowedArtistsPageContainer.class,
        Map.of(LIMIT_PARAMETER_NAME, LIMIT)
    );

    SpotifyFollowedArtistsPageContainer result = responseEntity.getBody();
    var shouldNotHappen = result == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get artists from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }
    return result.getArtistsPage();
  }
}
