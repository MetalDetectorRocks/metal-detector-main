package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.spotify.SpotifyFetchType;
import rocks.metaldetector.service.spotify.SpotifySynchronizationService;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.web.api.response.SpotifyArtistSynchronizationResponse;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION;

@RestController
@AllArgsConstructor
public class SpotifySynchronizationRestController {

  private final SpotifySynchronizationService spotifySynchronizationService;

  @PostMapping(path = SPOTIFY_ARTIST_SYNCHRONIZATION, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<SpotifyArtistSynchronizationResponse> synchronizeArtists() {
    List<String> savedArtistIds = spotifySynchronizationService.fetchSavedArtists(Arrays.stream(SpotifyFetchType.values()).toList())
        .stream().map(SpotifyArtistDto::getId).toList();
    List<String> followedArtistNames = spotifySynchronizationService.synchronizeArtists(savedArtistIds);
    return ResponseEntity.ok(new SpotifyArtistSynchronizationResponse(followedArtistNames));
  }
}
