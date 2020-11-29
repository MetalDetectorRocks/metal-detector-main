package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.spotify.SpotifyFetchType;
import rocks.metaldetector.service.spotify.SpotifySynchronizationService;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.request.SynchronizeArtistsRequest;
import rocks.metaldetector.web.api.response.SpotifyArtistSynchronizationResponse;
import rocks.metaldetector.web.api.response.SpotifyFetchArtistsResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@AllArgsConstructor
public class SpotifySynchronizationRestController {

  static final String FETCH_TYPES_PARAM = "fetchTypes";

  private final SpotifySynchronizationService spotifySynchronizationService;

  @PostMapping(path = Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION,
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyArtistSynchronizationResponse> synchronizeArtists(@Valid @RequestBody SynchronizeArtistsRequest request) {
    int artistsCount = spotifySynchronizationService.synchronizeArtists(request.getArtistIds());
    return ResponseEntity.ok(new SpotifyArtistSynchronizationResponse(artistsCount));
  }

  @GetMapping(path = Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyFetchArtistsResponse> fetchNotFollowedSpotifyArtists(@RequestParam(value = FETCH_TYPES_PARAM) @NotEmpty List<SpotifyFetchType> fetchTypes) {
    List<SpotifyArtistDto> followedArtists = spotifySynchronizationService.fetchNotFollowedArtists(fetchTypes);
    return ResponseEntity.ok(new SpotifyFetchArtistsResponse(followedArtists));
  }
}
