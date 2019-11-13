package com.metalr2.service.followArtist;

import com.metalr2.web.dto.FollowArtistDto;

public interface FollowArtistService {

  FollowArtistDto followArtist(FollowArtistDto followArtistDto);

  boolean unfollowArtist(FollowArtistDto followArtistDto);

  boolean followArtistEntityExists(FollowArtistDto followArtistDto);

}
