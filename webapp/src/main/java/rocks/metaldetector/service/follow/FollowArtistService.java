package rocks.metaldetector.service.follow;

import rocks.metaldetector.service.artist.ArtistDto;

import java.util.List;

public interface FollowArtistService {

  void follow(long artistId);
  void unfollow(long artistId);
  boolean isFollowed(long artistId);

  List<ArtistDto> findFollowedArtists();
  List<ArtistDto> findFollowedArtistsForUser(String publicUserId);
  long countFollowedArtists();
}
