package rocks.metaldetector.service.artist;

import java.util.List;

public interface FollowArtistService {

  void follow(long artistId);
  void unfollow(long artistId);
  List<ArtistDto> getFollowedArtistsOfCurrentUser();
}
