package rocks.metaldetector.service.spotify;

import rocks.metaldetector.service.artist.ArtistDto;

import java.util.List;

public interface SpotifyFollowedArtistsService {

  List<ArtistDto> importArtistsFromLikedReleases();

  List<ArtistDto> getNewFollowedArtists(List<String> importTypes);
}
