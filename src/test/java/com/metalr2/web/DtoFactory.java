package com.metalr2.web;

import com.metalr2.model.artist.ArtistEntity;
import com.metalr2.model.artist.FollowedArtistEntity;
import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.discogs.artist.DiscogsArtist;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResult;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.DiscogsPagination;
import com.metalr2.web.dto.request.ChangePasswordRequest;
import com.metalr2.web.dto.request.RegisterUserRequest;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.MyArtistsResponse;
import com.metalr2.web.dto.response.Pagination;
import com.metalr2.web.dto.response.SearchResponse;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class DtoFactory {

  private static final long DISCOGS_ID = 252211L;
  private static final String ARTIST_NAME = "Darkthrone";

  public static class UserDtoFactory {

    public static UserDto withUsernameAndEmail(String username, String email) {
      return UserDto.builder()
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

  public static class DiscogsArtistSearchResultFactory {

    public static DiscogsArtistSearchResultContainer withOneResult() {
      return createDefaultResultContainer();
    }

    public static DiscogsArtistSearchResultContainer withOneCertainResult() {
      DiscogsArtistSearchResultContainer container = createDefaultResultContainer();
      DiscogsArtistSearchResult searchResult = new DiscogsArtistSearchResult();
      DiscogsPagination pagination = new DiscogsPagination();

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

  public static class ArtistDetailsResponseFactory {

    public static ArtistDetailsResponse withResult() {
      return new ArtistDetailsResponse(ARTIST_NAME, DISCOGS_ID, null,
                                       null, null, null, false);
    }
  }

  public static class ArtistNameSearchResponseFactory {

    public static SearchResponse withOneResult() {
      return new SearchResponse(Collections.singletonList(
          new SearchResponse.SearchResult(null, DISCOGS_ID, ARTIST_NAME, false)),
                                          new Pagination(2, 1, 10));
    }
  }

  public static class MyArtistsResponseFactory {

    public static MyArtistsResponse withOneResult() {
      return new MyArtistsResponse(Collections.singletonList(
          new MyArtistsResponse.Artist(DISCOGS_ID, ARTIST_NAME, null)),
                                   new Pagination());
    }

    public static MyArtistsResponse withEmptyResult() {
      return new MyArtistsResponse(Collections.emptyList(), null);
    }
  }

  public static class ArtistFactory {

    public static DiscogsArtist createTestArtist() {
      DiscogsArtist discogsArtist = new DiscogsArtist();
      discogsArtist.setId(252211L);
      discogsArtist.setName("Darkthrone");
      discogsArtist.setProfile("profile");
      return discogsArtist;
    }
  }

  public static class FollowArtistFactory {

    public static FollowedArtistEntity createFollowArtistEntity(String userId, long discogsId) {
      return new FollowedArtistEntity(userId, discogsId);
    }

    public static List<FollowedArtistEntity> createFollowArtistEntities(int amount) {
      return LongStream.range(1, amount+1).mapToObj(entity -> createFollowArtistEntity("1", entity)).collect(Collectors.toList());
    }
  }

  public static class ArtistEntityFactory {

    public static ArtistEntity createArtistEntity(long discogsId, String artistName, String thumb) {
      return new ArtistEntity(discogsId, artistName, thumb);
    }

    public static List<ArtistEntity> createArtistEntities(int amount) {
      return LongStream.range(1, amount+1).mapToObj(entity -> createArtistEntity(entity,String.valueOf(entity),null)).collect(Collectors.toList());
    }

    public static List<ArtistEntity> createArtistEntities(int amount, int offset) {
      return LongStream.range(1, amount+1).mapToObj(entity -> createArtistEntity(entity+offset,String.valueOf(entity+offset),null)).collect(Collectors.toList());
    }
  }
}
