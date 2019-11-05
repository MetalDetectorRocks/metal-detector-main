package com.metalr2.web.controller;

import javax.validation.Valid;

/**
 * Interface for following and unfollowing artists
 * @param <U> DTO for request
 */
public interface FollowArtistsRestController<U> {

  boolean followArtist(@Valid U request);

  void unfollowArtist(@Valid U request);

}
