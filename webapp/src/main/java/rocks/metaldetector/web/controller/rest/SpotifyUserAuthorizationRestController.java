package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

@RestController
@RequestMapping(Endpoints.Rest.SPOTIFY_AUTHORIZATION)
@AllArgsConstructor
public class SpotifyUserAuthorizationRestController {

  private final SpotifyUserAuthorizationService spotifyUserAuthorizationService;

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyUserAuthorizationResponse> prepareUserAuthorization() {
    String authorizationUrl = spotifyUserAuthorizationService.prepareAuthorization();
    return ResponseEntity.ok().body(new SpotifyUserAuthorizationResponse(authorizationUrl));
  }
}
