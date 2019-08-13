package com.metalr2.service.followArtist;

import com.metalr2.web.dto.request.FollowArtistRequest;

public interface FollowArtistService {

  boolean followArtist(FollowArtistRequest followArtistRequest);

  boolean unfollowArtist(FollowArtistRequest unfollowArtistRequest);

}
