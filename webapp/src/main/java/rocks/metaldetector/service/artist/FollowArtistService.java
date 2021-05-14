package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.List;

public interface FollowArtistService {

  void follow(String externalArtistId, ArtistSource source);

  int followSpotifyArtists(List<String> spotifyArtistIds);

  void unfollow(String externalArtistId, ArtistSource source);

  boolean isCurrentUserFollowing(String externalArtistId, ArtistSource source);

  List<ArtistDto> getFollowedArtistsOfCurrentUser();

  List<ArtistDto> getFollowedArtistsOfUser(AbstractUserEntity user);

}
