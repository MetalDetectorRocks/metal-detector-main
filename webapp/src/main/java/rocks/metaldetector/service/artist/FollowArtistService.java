package rocks.metaldetector.service.artist;

import java.util.List;

public interface FollowArtistService {

  void follow(String externalId, String source);
  void unfollow(String externalId);
  List<ArtistDto> getFollowedArtistsOfCurrentUser();
  List<ArtistDto> getFollowedArtistsOfUser(String publicUserId);
}
