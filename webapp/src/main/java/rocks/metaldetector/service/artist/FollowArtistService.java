package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;

public interface FollowArtistService {

  void follow(String externalArtistId, ArtistSource source);

  void follow(SpotifyArtistDto spotifyArtistDto);

  void unfollow(String externalArtistId, ArtistSource source);

  boolean isCurrentUserFollowing(String externalArtistId, ArtistSource source);

  List<ArtistDto> getFollowedArtistsOfCurrentUser();

  List<ArtistDto> getFollowedArtistsOfUser(String publicUserId);
}
