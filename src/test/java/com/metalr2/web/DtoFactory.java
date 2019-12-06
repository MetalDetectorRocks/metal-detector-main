package com.metalr2.web;

import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.discogs.artist.DiscogsArtist;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResult;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.DiscogsPagination;
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

    public static DiscogsArtistSearchResultContainer withOneResult() {
      return createDefaultResultContainer();
    }

    public static DiscogsArtistSearchResultContainer withOneCertainResult() {
      DiscogsArtistSearchResultContainer container  = createDefaultResultContainer();
      DiscogsArtistSearchResult searchResult        = new DiscogsArtistSearchResult();
      DiscogsPagination pagination                  = new DiscogsPagination();

      searchResult.setId(252211);
      searchResult.setTitle("Darkthrone");

      pagination.setCurrentPage(1);
      pagination.setItemsPerPage(10);
      pagination.setItemsTotal(10);
      pagination.setPagesTotal(2);

      container.setResults(Collections.singletonList(searchResult));
      container.setDiscogsPagination(pagination);
      return container;
    }

    public static DiscogsArtistSearchResultContainer withEmptyResult() {
      DiscogsArtistSearchResultContainer resultContainer = createDefaultResultContainer();
      resultContainer.setResults(Collections.emptyList());

      return resultContainer;
    }

    private static DiscogsArtistSearchResultContainer createDefaultResultContainer() {
      DiscogsPagination discogsPagination = new DiscogsPagination();
      DiscogsArtistSearchResult discogsArtistSearchResult = new DiscogsArtistSearchResult();

      DiscogsArtistSearchResultContainer resultContainer = new DiscogsArtistSearchResultContainer();
      resultContainer.setDiscogsPagination(discogsPagination);
      resultContainer.setResults(List.of(discogsArtistSearchResult));

      return resultContainer;
    }

  }

  public static class ArtistFactory {

    public static DiscogsArtist createTestArtist() {
      DiscogsArtist discogsArtist = new DiscogsArtist();
      discogsArtist.setId(1L);
      discogsArtist.setProfile("profile");
      return discogsArtist;
    }

  }

}
