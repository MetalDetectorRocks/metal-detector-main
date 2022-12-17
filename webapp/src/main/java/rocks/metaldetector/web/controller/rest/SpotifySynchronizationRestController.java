package rocks.metaldetector.web.controller.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.spotify.SpotifyFetchType;
import rocks.metaldetector.service.spotify.SpotifySynchronizationService;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.web.api.request.SynchronizeArtistsRequest;
import rocks.metaldetector.web.api.response.SpotifyArtistSynchronizationResponse;
import rocks.metaldetector.web.api.response.SpotifyFetchArtistsResponse;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION;
import static rocks.metaldetector.support.Endpoints.Rest.SPOTIFY_SAVED_ARTISTS;

@RestController
@AllArgsConstructor
public class SpotifySynchronizationRestController {

  static final String FETCH_TYPES_PARAM = "fetchTypes";

  private final SpotifySynchronizationService spotifySynchronizationService;

  @PostMapping(path = SPOTIFY_ARTIST_SYNCHRONIZATION, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<SpotifyArtistSynchronizationResponse> synchronizeArtists(@Valid @RequestBody SynchronizeArtistsRequest request) {
    int artistsCount = spotifySynchronizationService.synchronizeArtists(request.getArtistIds());
    return ResponseEntity.ok(new SpotifyArtistSynchronizationResponse(artistsCount));
  }

  @GetMapping(path = SPOTIFY_SAVED_ARTISTS, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<SpotifyFetchArtistsResponse> fetchSavedSpotifyArtists(@RequestParam(value = FETCH_TYPES_PARAM) @NotEmpty List<SpotifyFetchType> fetchTypes) {
    List<SpotifyArtistDto> savedArtists = spotifySynchronizationService.fetchSavedArtists(fetchTypes);
    return ResponseEntity.ok(new SpotifyFetchArtistsResponse(savedArtists));
  }
}
