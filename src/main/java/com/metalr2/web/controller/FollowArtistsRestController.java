package com.metalr2.web.controller;

import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.RestRequestValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;

/**
 * Interface for following and unfollowing artists
 * @param <T> DTO for request
 * @param <U> DTO for response
 */
public interface FollowArtistsRestController<T, U> {

  ResponseEntity<U> followArtist(@Valid T request, BindingResult bindingResult);

  void unfollowArtist(@Valid T request, BindingResult bindingResult);

  default void validateRequest(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RestRequestValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }
  }
}
