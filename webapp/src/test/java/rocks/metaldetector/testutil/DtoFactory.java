package rocks.metaldetector.testutil;

import rocks.metaldetector.butler.facade.dto.ImportResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.web.api.request.ChangePasswordRequest;
import rocks.metaldetector.web.api.request.DetectorReleasesRequest;
import rocks.metaldetector.web.api.request.RegisterUserRequest;
import rocks.metaldetector.web.api.request.UpdateUserRequest;
import rocks.metaldetector.web.api.response.DetectorImportResponse;

import java.time.LocalDate;
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
          .followedArtists(List.of(ArtistDtoFactory.createDefault()))
          .build();
    }
    public static UserDto createUser(String username, UserRole role, boolean enabled) {
      return UserDto.builder()
          .username(username)
          .enabled(enabled)
          .role(role.getDisplayName())
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
      return UpdateUserRequest.builder()
              .publicUserId("abc-123")
              .role("USER")
              .enabled(true)
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

  public static class DetectorReleaseRequestFactory {

    public static DetectorReleasesRequest createDefault() {
      return DetectorReleasesRequest.builder()
              .artists(List.of("A", "B", "C"))
              .dateFrom(LocalDate.now())
              .dateTo(LocalDate.now().plusDays(30))
              .build();
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

    static DiscogsArtistSearchResultEntryDto createDefault() {
      return withId(DISCOGS_ID);
    }

    public static DiscogsArtistSearchResultEntryDto withId(long discogsId) {
      return DiscogsArtistSearchResultEntryDto.builder()
              .id(discogsId)
              .name(ARTIST_NAME)
              .build();
    }
  }

  public static class DiscogsArtistDtoFactory {

    public static DiscogsArtistDto createDefault() {
      return DiscogsArtistDto.builder()
              .id(DISCOGS_ID)
              .name(ARTIST_NAME)
              .build();
    }
  }

  public static class ReleaseDtoFactory {

    public static ReleaseDto createDefault() {
      return withArtistName("Evil Artist");
    }

    public static ReleaseDto withArtistName(String artistName) {
      return ReleaseDto.builder()
              .artist(artistName)
              .albumTitle("Heavy Release")
              .releaseDate(LocalDate.now().plusDays(10))
              .build();
    }
  }

  public static class ImportResultDtoFactory {

    public static ImportResultDto createDefault() {
      return ImportResultDto.builder()
          .totalCountImported(666)
          .totalCountRequested(666)
          .build();
    }
  }

  public static class DetectorImportResponseFactory {

    public static DetectorImportResponse createDefault() {
      return DetectorImportResponse.builder()
          .totalCountRequested(666)
          .totalCountImported(666)
          .build();
    }
  }

  public static class ArtistDtoFactory {

    public static ArtistDto createDefault() {
      return withName("A");
    }

    public static ArtistDto withName(String name) {
      return ArtistDto.builder()
              .artistName(name)
              .discogsId(1L)
              .build();
    }
  }
}
