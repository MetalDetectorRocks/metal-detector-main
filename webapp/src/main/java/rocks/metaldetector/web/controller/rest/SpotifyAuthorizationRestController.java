package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.spotify.SpotifyUserAuthorizationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.request.SpotifyAuthorizationRequest;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationExistsResponse;
import rocks.metaldetector.web.api.response.SpotifyUserAuthorizationResponse;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class SpotifyAuthorizationRestController {

  private final SpotifyUserAuthorizationService userAuthorizationService;

  @GetMapping(path = Endpoints.Rest.SPOTIFY_AUTHORIZATION,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyUserAuthorizationExistsResponse> checkAuthorization() {
    boolean exists = userAuthorizationService.exists();
    return ResponseEntity.ok(new SpotifyUserAuthorizationExistsResponse(exists));
  }

  @PostMapping(path = Endpoints.Rest.SPOTIFY_AUTHORIZATION,
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyUserAuthorizationResponse> prepareAuthorization() {
    String authorizationUrl = userAuthorizationService.prepareAuthorization();
    return ResponseEntity.ok().body(new SpotifyUserAuthorizationResponse(authorizationUrl));
  }

  @PutMapping(path = Endpoints.Rest.SPOTIFY_AUTHORIZATION,
              consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> updateAuthorization(@Valid @RequestBody SpotifyAuthorizationRequest spotifyAuthorizationRequest) {
    userAuthorizationService.persistInitialToken(spotifyAuthorizationRequest.getState(), spotifyAuthorizationRequest.getCode());
    return ResponseEntity.ok().build();
  }
}
