package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.spotify.SpotifyArtistImportService;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.response.SpotifyArtistImportResponse;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

import java.util.List;

@RestController
@AllArgsConstructor
public class SpotifyRestController {

  private final SpotifyUserAuthorizationService userAuthorizationService;
  private final SpotifyArtistImportService artistImportService;

  @GetMapping(path = Endpoints.Rest.SPOTIFY_AUTHORIZATION,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyUserAuthorizationResponse> prepareUserAuthorization() {
    String authorizationUrl = userAuthorizationService.prepareAuthorization();
    return ResponseEntity.ok().body(new SpotifyUserAuthorizationResponse(authorizationUrl));
  }

  @PostMapping(path = Endpoints.Rest.SPOTIFY_ARTIST_IMPORT,
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyArtistImportResponse> importArtists() {
    List<ArtistDto> artists = artistImportService.importArtistsFromLikedReleases();
    return ResponseEntity.ok(new SpotifyArtistImportResponse(artists));
  }
}
