package rocks.metaldetector.spotify.facade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.authorization.SpotifyUserAuthorizationResponse;
import rocks.metaldetector.spotify.api.imports.SpotifyFollowedArtistsPage;
import rocks.metaldetector.spotify.api.imports.SpotifySavedAlbumsPage;
import rocks.metaldetector.spotify.api.imports.SpotifySavedAlbumsPageItem;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;
import rocks.metaldetector.spotify.client.SpotifyArtistSearchClient;
import rocks.metaldetector.spotify.client.SpotifyAuthorizationClient;
import rocks.metaldetector.spotify.client.SpotifyUserLibraryClient;
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
import rocks.metaldetector.support.SlicingService;

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
  static final int PAGE_SIZE = 50;

  private final SpotifyArtistSearchClient artistSearchClient;
  private final SpotifyAuthorizationClient authorizationClient;
  private final SpotifyUserLibraryClient importClient;
  private final SpotifyArtistSearchResultTransformer searchResultTransformer;
  private final SpotifyArtistTransformer artistTransformer;
  private final SpotifyUserAuthorizationTransformer userAuthorizationTransformer;
  private final SpotifyAlbumTransformer albumTransformer;
  private final SpotifyProperties spotifyProperties;
  private final SlicingService slicingService;

  @Override
  public SpotifyArtistSearchResultDto searchArtistByName(String artistQueryString, int pageNumber, int pageSize) {
    SpotifyArtistSearchResultContainer searchResult = artistSearchClient.searchByName(artistQueryString, pageNumber, pageSize);
    return searchResultTransformer.transform(searchResult);
  }

  @Override
  public SpotifyArtistDto searchArtistById(String artistId) {
    SpotifyArtist spotifyArtist = artistSearchClient.searchById(artistId);
    return artistTransformer.transform(spotifyArtist);
  }

  @Override
  public List<SpotifyArtistDto> searchArtistsByIds(List<String> artistIds) {
    List<SpotifyArtist> spotifyArtists = new ArrayList<>();
    int totalPages = (int) Math.ceil((double) artistIds.size() / (double) PAGE_SIZE);
    for (int i = 1; i <= totalPages; i++) {
      List<String> idsPerPage = slicingService.slice(artistIds, i, PAGE_SIZE);
      SpotifyArtistsContainer spotifyArtistsContainer = artistSearchClient.searchByIds(idsPerPage);
      spotifyArtists.addAll(spotifyArtistsContainer.getArtists());
    }
    return spotifyArtists.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public String getSpotifyAuthorizationUrl() {
    String endpoint = String.format(USER_AUTHORIZATION_ENDPOINT,
                                    spotifyProperties.getClientId(),
                                    spotifyProperties.getApplicationHostUrl() + Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION + Endpoints.Frontend.SPOTIFY_CALLBACK,
                                    URLEncoder.encode(String.join(" ", REQUIRED_SCOPES), Charset.defaultCharset()));
    return spotifyProperties.getAuthenticationBaseUrl() + endpoint;
  }

  @Override
  public SpotifyUserAuthorizationDto getAccessToken(String code) {
    SpotifyUserAuthorizationResponse response = authorizationClient.getUserAuthorizationToken(code);
    return userAuthorizationTransformer.transform(response);
  }

  @Override
  public List<SpotifyAlbumDto> fetchLikedAlbums(String token) {
    int offset = 0;
    List<SpotifySavedAlbumsPageItem> resultItems = new ArrayList<>();
    SpotifySavedAlbumsPage importResult;
    do {
      importResult = importClient.fetchLikedAlbums(token, offset);
      resultItems.addAll(importResult.getItems());
      offset += importResult.getLimit();
    }
    while (offset < importResult.getTotal());

    return resultItems.stream()
        .map(SpotifySavedAlbumsPageItem::getAlbum)
        .map(albumTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public List<SpotifyArtistDto> fetchFollowedArtists(String token) {
    String nextPage = null;
    List<SpotifyArtist> resultItems = new ArrayList<>();
    SpotifyFollowedArtistsPage importResult;
    do {
      importResult = importClient.fetchFollowedArtists(token, nextPage);
      resultItems.addAll(importResult.getItems());
      nextPage = importResult.getNext();
    }
    while (nextPage != null);

    return resultItems.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public SpotifyUserAuthorizationDto refreshToken(String refreshToken) {
    SpotifyUserAuthorizationResponse response = authorizationClient.refreshUserAuthorizationToken(refreshToken);
    return userAuthorizationTransformer.transform(response);
  }
}
