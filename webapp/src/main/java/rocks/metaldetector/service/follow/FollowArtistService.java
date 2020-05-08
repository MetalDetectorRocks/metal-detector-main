package rocks.metaldetector.service.follow;

public interface FollowArtistService {

  void follow(long artistId);
  void unfollow(long artistId);

}
