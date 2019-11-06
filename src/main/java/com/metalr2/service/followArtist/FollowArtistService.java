package com.metalr2.service.followArtist;

import com.metalr2.web.dto.FollowArtistsDto;

public interface FollowArtistService {

  FollowArtistsDto followArtist(FollowArtistsDto followArtistsDto);

  void unfollowArtist(FollowArtistsDto followArtistsDto);

  boolean userFollowsArtist(FollowArtistsDto followArtistsDto);

  boolean artistFollowedByUser(FollowArtistsDto followArtistsDto);

}
