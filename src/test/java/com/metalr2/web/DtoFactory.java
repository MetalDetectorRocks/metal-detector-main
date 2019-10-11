package com.metalr2.web;

import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.discogs.artist.Artist;
import com.metalr2.web.dto.discogs.search.ArtistSearchResult;
import com.metalr2.web.dto.discogs.search.ArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.Pagination;
import com.metalr2.web.dto.request.ChangePasswordRequest;
import com.metalr2.web.dto.request.RegisterUserRequest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DtoFactory {

  public static class UserDtoFactory {

    public static UserDto withUsernameAndEmail(String username, String email) {
      return UserDto.builder()
              .id(1)
              .publicId(UUID.randomUUID().toString())
              .username(username)
              .email(email)
              .plainPassword("xxx")
              .enabled(true)
              .build();
    }

  }

  public static class RegisterUserRequestFactory {

    public static RegisterUserRequest withUsername(String username) {
      return create(username, "john.d@example.com", "valid-password", "valid-password");
    }

    public static RegisterUserRequest withEmail(String email) {
      return create("JohnD", email, "valid-password", "valid-password");
    }

    public static RegisterUserRequest withPassword(String plainPassword, String verifyPlainPassword) {
      return create("JohnD", "john.d@example.com", plainPassword, verifyPlainPassword);
    }

    private static RegisterUserRequest create(String username, String email, String plainPassword, String verifyPlainPassword) {
      return RegisterUserRequest.builder()
              .username(username)
              .email(email)
              .plainPassword(plainPassword)
              .verifyPlainPassword(verifyPlainPassword)
              .build();
    }

  }

  public static class ChangePasswordRequestFactory {

    public static ChangePasswordRequest withTokenString(String tokenString) {
      return create(tokenString, "valid-password", "valid-password");
    }

    public static ChangePasswordRequest withPassword(String plainPassword, String verifyPlainPassword) {
      return create("valid-token", plainPassword, verifyPlainPassword);
    }

    private static ChangePasswordRequest create(String tokenString, String plainPassword, String verifyPlainPassword) {
      return ChangePasswordRequest.builder()
              .tokenString(tokenString)
              .newPlainPassword(plainPassword)
              .verifyNewPlainPassword(verifyPlainPassword)
              .build();
    }

  }

  public static class ArtistSearchResultContainerFactory {

    public static ArtistSearchResultContainer withOneResult() {
      return createDefaultResultContainer();
    }

    public static ArtistSearchResultContainer withEmptyResult() {
      ArtistSearchResultContainer resultContainer = createDefaultResultContainer();
      resultContainer.setResults(Collections.emptyList());

      return resultContainer;
    }

    private static ArtistSearchResultContainer createDefaultResultContainer() {
      Pagination         pagination         = new Pagination();
      ArtistSearchResult artistSearchResult = new ArtistSearchResult();

      ArtistSearchResultContainer resultContainer = new ArtistSearchResultContainer();
      resultContainer.setPagination(pagination);
      resultContainer.setResults(List.of(artistSearchResult));

      return resultContainer;
    }

  }

  public static class ArtistFactory {

    public static Artist createTestArtist() {
      Artist artist = new Artist();
      artist.setId(1L);
      artist.setProfile("profile");
      return artist;
    }

  }

}
