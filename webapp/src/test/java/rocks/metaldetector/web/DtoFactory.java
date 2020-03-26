package rocks.metaldetector.web;

import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsPagination;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.web.api.request.ChangePasswordRequest;
import rocks.metaldetector.web.api.request.DetectorReleasesRequest;
import rocks.metaldetector.web.api.request.RegisterUserRequest;
import rocks.metaldetector.web.api.request.UpdateUserRequest;
import rocks.metaldetector.web.api.response.DetectorReleasesResponse;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.web.api.response.SearchResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// ToDo DanielW: Hier am Abschluss einmal aufräumen
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

  public static class DetectorReleaseRequestFactory {

    public static DetectorReleasesRequest createDefault() {
      return DetectorReleasesRequest.builder()
              .artists(List.of("A", "B", "C"))
              .dateFrom(LocalDate.now())
              .dateTo(LocalDate.now().plusDays(30))
              .build();
    }
  }

  public static class DetectorReleaseResponseFactory {

    public static DetectorReleasesResponse withOneResult(String artist, LocalDate releaseDate) {
      return new DetectorReleasesResponse(artist, Collections.singletonList(artist), "T", releaseDate, "releaseDate", false);
    }
  }

  public static class DiscogsArtistSearchResultDtoFactory {

    public static DiscogsArtistSearchResultDto createDefault() {
      Pagination pagination = Pagination.builder()
              .totalPages(10)
              .itemsPerPage(10)
              .currentPage(1)
              .build();

      return DiscogsArtistSearchResultDto.builder()
              .searchResults(List.of(DiscogsArtistSearchResultEntryDtoFactory.createDefault()))
              .pagination(pagination)
              .build();
    }
  }

  public static class DiscogsArtistSearchResultEntryDtoFactory {

    public static DiscogsArtistSearchResultEntryDto createDefault() {
      return DiscogsArtistSearchResultEntryDto.builder()
              .id(666)
              .name("A")
              .build();
    }
  }

  public static class DiscogsArtistDtoFactory {

    public static DiscogsArtistDto createTestArtist() {
      return DiscogsArtistDto.builder()
              .id(252211L)
              .name("Darkthrone")
              .build();
    }
  }
}
