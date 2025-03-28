package rocks.metaldetector.spotify.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
@Profile({"default", "preview", "prod"})
public class SpotifyArtistSearchClientImpl implements SpotifyArtistSearchClient {

  static final String ID_PARAMETER_NAME = "id";
  static final String QUERY_PARAMETER_NAME = "artistQueryString";
  static final String OFFSET_PARAMETER_NAME = "offset";
  static final String LIMIT_PARAMETER_NAME = "limit";

  static final String GET_ARTIST_ENDPOINT = "/v1/artists/{" + ID_PARAMETER_NAME + "}";
  static final String GET_ARTISTS_ENDPOINT = "/v1/artists?ids={" + ID_PARAMETER_NAME + "}";
  static final String SEARCH_ENDPOINT = "/v1/search?q={" + QUERY_PARAMETER_NAME + "}&"
      + "type=artist&offset={" + OFFSET_PARAMETER_NAME + "}&"
      + "limit={" + LIMIT_PARAMETER_NAME + "}";

  private final RestOperations spotifyOAuthClientCredentialsRestTemplate;
  private final SpotifyProperties spotifyProperties;

  public SpotifyArtistSearchClientImpl(@Qualifier("spotifyOAuthClientCredentialsRestTemplate") RestOperations spotifyOAuthClientCredentialsRestTemplate,
                                       SpotifyProperties spotifyProperties) {
    this.spotifyOAuthClientCredentialsRestTemplate = spotifyOAuthClientCredentialsRestTemplate;
    this.spotifyProperties = spotifyProperties;
  }

  @Override
  public SpotifyArtistSearchResultContainer searchByName(String artistQueryString, int pageNumber, int pageSize) {
    HttpEntity<Object> httpEntity = new HttpEntity<>(null);
    int offset = pageSize * (pageNumber - 1);

    ResponseEntity<SpotifyArtistSearchResultContainer> responseEntity = spotifyOAuthClientCredentialsRestTemplate.exchange(
        spotifyProperties.getRestBaseUrl() + SEARCH_ENDPOINT,
        GET,
        httpEntity,
        SpotifyArtistSearchResultContainer.class,
        Map.of(QUERY_PARAMETER_NAME, artistQueryString,
            OFFSET_PARAMETER_NAME, offset,
            LIMIT_PARAMETER_NAME, pageSize)
    );

    SpotifyArtistSearchResultContainer resultContainer = responseEntity.getBody();
    var shouldNotHappen = resultContainer == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get search results for query '" + artistQueryString + "' (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return resultContainer;
  }

  @Override
  public SpotifyArtist searchById(String artistId) {
    if (artistId == null || artistId.isEmpty()) {
      throw new IllegalArgumentException("externalId must not be empty");
    }

    HttpEntity<Object> httpEntity = new HttpEntity<>(null);
    ResponseEntity<SpotifyArtist> responseEntity = spotifyOAuthClientCredentialsRestTemplate.exchange(
        spotifyProperties.getRestBaseUrl() + GET_ARTIST_ENDPOINT,
        GET,
        httpEntity,
        SpotifyArtist.class,
        Map.of(ID_PARAMETER_NAME, artistId)
    );

    SpotifyArtist spotifyArtist = responseEntity.getBody();
    var shouldNotHappen = spotifyArtist == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get artist with id '" + artistId + "' from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return spotifyArtist;
  }

  @Override
  public SpotifyArtistsContainer searchByIds(List<String> artistIds) {
    if (artistIds == null || artistIds.isEmpty()) {
      throw new IllegalArgumentException("artistIds must not be empty");
    }

    HttpEntity<Object> httpEntity = new HttpEntity<>(null);
    String artistIdsString = String.join(",", artistIds);
    ResponseEntity<SpotifyArtistsContainer> responseEntity = spotifyOAuthClientCredentialsRestTemplate.exchange(
        spotifyProperties.getRestBaseUrl() + GET_ARTISTS_ENDPOINT,
        GET,
        httpEntity,
        SpotifyArtistsContainer.class,
        Map.of(ID_PARAMETER_NAME, artistIdsString)
    );

    SpotifyArtistsContainer spotifyArtistsContainer = responseEntity.getBody();
    var shouldNotHappen = spotifyArtistsContainer == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not get artists from Spotify (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return spotifyArtistsContainer;
  }
}
