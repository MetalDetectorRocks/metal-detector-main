package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.spotify.SpotifyFetchType;
import rocks.metaldetector.service.spotify.SpotifyFollowedArtistsService;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.response.SpotifyArtistImportResponse;
import rocks.metaldetector.web.api.response.SpotifyFollowedArtistsResponse;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@AllArgsConstructor
public class SpotifyRestController {

  static final String FETCH_TYPES_PARAM = "fetchTypes";

  private final SpotifyUserAuthorizationService userAuthorizationService;
  private final SpotifyFollowedArtistsService spotifyFollowedArtistsService;

  @PostMapping(path = Endpoints.Rest.SPOTIFY_AUTHORIZATION,
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyUserAuthorizationResponse> prepareUserAuthorization() {
    String authorizationUrl = userAuthorizationService.prepareAuthorization();
    return ResponseEntity.ok().body(new SpotifyUserAuthorizationResponse(authorizationUrl));
  }

  @PostMapping(path = Endpoints.Rest.SPOTIFY_ARTIST_IMPORT,
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyArtistImportResponse> importArtists() {
    List<ArtistDto> artists = spotifyFollowedArtistsService.importArtistsFromLikedReleases();
    return ResponseEntity.ok(new SpotifyArtistImportResponse(artists));
  }

  @GetMapping(path = Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyFollowedArtistsResponse> getFollowedArtists(@RequestParam(value = FETCH_TYPES_PARAM) @NotEmpty List<SpotifyFetchType> fetchTypes) {
    List<ArtistDto> followedArtists = spotifyFollowedArtistsService.getNewFollowedArtists(fetchTypes);
    return ResponseEntity.ok(new SpotifyFollowedArtistsResponse(followedArtists));
  }
}
