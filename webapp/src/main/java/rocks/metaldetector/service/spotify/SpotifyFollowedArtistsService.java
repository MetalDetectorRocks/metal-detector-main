package rocks.metaldetector.service.spotify;

import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;

public interface SpotifyFollowedArtistsService {

  List<ArtistDto> importArtistsFromLikedReleases();

  List<SpotifyArtistDto> getNewFollowedArtists(List<SpotifyFetchType> fetchTypes);
}
