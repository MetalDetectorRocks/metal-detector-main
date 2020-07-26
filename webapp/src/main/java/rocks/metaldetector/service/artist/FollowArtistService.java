package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistSource;

import java.util.List;

public interface FollowArtistService {

  void follow(String externalId, ArtistSource source);
  void unfollow(String externalId, ArtistSource source);
  List<ArtistDto> getFollowedArtistsOfCurrentUser();
  List<ArtistDto> getFollowedArtistsOfUser(String publicUserId);
}
