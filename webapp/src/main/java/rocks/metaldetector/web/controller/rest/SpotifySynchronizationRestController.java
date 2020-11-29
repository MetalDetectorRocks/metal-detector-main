package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.spotify.SpotifyFetchType;
import rocks.metaldetector.service.spotify.SpotifyFollowedArtistsService;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.request.SynchronizeArtistsRequest;
import rocks.metaldetector.web.api.response.SpotifyArtistImportResponse;
import rocks.metaldetector.web.api.response.SpotifyFollowedArtistsResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;

@RestController
@AllArgsConstructor
public class SpotifySynchronizationRestController {

  static final String FETCH_TYPES_PARAM = "fetchTypes";

  private final SpotifyFollowedArtistsService spotifyFollowedArtistsService;

  @PostMapping(path = Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION,
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyArtistImportResponse> synchronizeArtists(@Valid @RequestBody SynchronizeArtistsRequest request) {
    List<ArtistDto> artists = Collections.emptyList(); // ToDo DanielW: Synchronize artists
    return ResponseEntity.ok(new SpotifyArtistImportResponse(artists));
  }

  @GetMapping(path = Endpoints.Rest.SPOTIFY_FOLLOWED_ARTISTS,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SpotifyFollowedArtistsResponse> getFollowedArtists(@RequestParam(value = FETCH_TYPES_PARAM) @NotEmpty List<SpotifyFetchType> fetchTypes) {
    List<SpotifyArtistDto> followedArtists = spotifyFollowedArtistsService.getNewFollowedArtists(fetchTypes);
    return ResponseEntity.ok(new SpotifyFollowedArtistsResponse(followedArtists));
  }
}
