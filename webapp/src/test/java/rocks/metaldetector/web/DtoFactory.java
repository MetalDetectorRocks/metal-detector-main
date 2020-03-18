package rocks.metaldetector.web;

import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsPagination;
import rocks.metaldetector.model.artist.ArtistEntity;
import rocks.metaldetector.web.dto.UserDto;
import rocks.metaldetector.web.dto.releases.ButlerReleasesResponse;
import rocks.metaldetector.web.dto.releases.ReleaseDto;
import rocks.metaldetector.web.dto.request.ChangePasswordRequest;
import rocks.metaldetector.web.dto.request.RegisterUserRequest;
import rocks.metaldetector.web.dto.request.UpdateUserRequest;
import rocks.metaldetector.web.dto.response.DetectorReleasesResponse;
import rocks.metaldetector.web.dto.response.Pagination;
import rocks.metaldetector.web.dto.response.SearchResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DtoFactory {

  private static final long DISCOGS_ID = 252211L;
  private static final String ARTIST_NAME = "Darkthrone";

  public static class UserDtoFactory {

    public static UserDto createDefault() {
      return withUsernameAndEmail("JohnD", "john.d@example.com");
    }

    public static UserDto withUsernameAndEmail(String username, String email) {
      return UserDto.builder()
          .publicId(UUID.randomUUID().toString())
          .username(username)
          .email(email)
          .plainPassword("xxx")
          .role("User")
          .enabled(true)
          .build();
    }
  }

  public static class RegisterUserRequestFactory {

    public static RegisterUserRequest createDefault() {
      return create("JohnD", "john.d@example.com", "valid-password", "valid-password");
    }

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

  public static class UpdateUserRequestFactory {

    public static UpdateUserRequest createDefault() {
      return create("abc-123", "USER", true);
    }

    private static UpdateUserRequest create(String publicUserId, String role, boolean enabled) {
      return UpdateUserRequest.builder()
              .publicUserId(publicUserId)
              .role(role)
              .enabled(enabled)
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

  public static class ArtistNameSearchResponseFactory {

    public static SearchResponse withOneResult() {
      return new SearchResponse(Collections.singletonList(
          new SearchResponse.SearchResult(null, DISCOGS_ID, ARTIST_NAME, false)),
                                new Pagination(1, 1, 10));
    }
  }

  public static class ArtistEntityFactory {

    public static ArtistEntity createArtistEntity(long discogsId, String artistName, String thumb) {
      return new ArtistEntity(discogsId, artistName, thumb);
    }
  }

  public static class ButlerReleasesResponseFactory {

    public static ButlerReleasesResponse withOneResult(String artist, LocalDate releaseDate) {
      return new ButlerReleasesResponse(Collections.singletonList(ReleaseDtoFactory.withOneResult(artist, releaseDate)));
    }

    public static ButlerReleasesResponse withEmptyResult() {
      return new ButlerReleasesResponse(Collections.emptyList());
    }
  }

  public static class ReleaseDtoFactory {

    public static ReleaseDto withOneResult(String artist, LocalDate releaseDate) {
      return new ReleaseDto(artist, Collections.singletonList(artist), "T", releaseDate, "releaseDate");
    }
  }

  public static class DetectorReleaseResponseFactory {

    public static DetectorReleasesResponse withOneResult(String artist, LocalDate releaseDate) {
      return new DetectorReleasesResponse(artist, Collections.singletonList(artist), "T", releaseDate, "releaseDate", false);
    }
  }

  public static class DiscogsArtistSearchResultFactory {

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

    private static DiscogsArtistSearchResultContainer createDefaultResultContainer() {
      DiscogsPagination discogsPagination = new DiscogsPagination();
      DiscogsArtistSearchResult discogsArtistSearchResult = new DiscogsArtistSearchResult();

      DiscogsArtistSearchResultContainer resultContainer = new DiscogsArtistSearchResultContainer();
      resultContainer.setDiscogsPagination(discogsPagination);
      resultContainer.setResults(List.of(discogsArtistSearchResult));

      return resultContainer;
    }
  }

  public static class DiscogsArtistFactory {

    public static DiscogsArtist createTestArtist() {
      DiscogsArtist discogsArtist = new DiscogsArtist();
      discogsArtist.setId(252211L);
      discogsArtist.setName("Darkthrone");
      discogsArtist.setProfile("profile");
      return discogsArtist;
    }
  }
}
