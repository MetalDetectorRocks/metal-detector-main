package com.metalr2.service.artist;

import com.metalr2.web.dto.ArtistDto;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;

import java.util.List;
import java.util.Optional;

public interface ArtistsService {

  Optional<ArtistDto> findByArtistDiscogsId(long discogsId);

  List<ArtistDto> findAllByArtistDiscogsIdIn(long... discogsIds);

  boolean existsByArtistDiscogsId(long discogsId);

  boolean followArtist(long discogsId);

  boolean unfollowArtist(long discogsId);

  boolean exists(long discogsId);

  List<FollowArtistDto> findPerUser(String publicUserId);

  Optional<ArtistNameSearchResponse> searchDiscogsByName(String artistQueryString, int page, int size);

  Optional<ArtistDetailsResponse> searchDiscogsById(long artistId);

}
