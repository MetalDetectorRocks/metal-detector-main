package rocks.metaldetector.testutil;

import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.web.api.request.ChangePasswordRequest;
import rocks.metaldetector.web.api.request.PaginatedReleasesRequest;
import rocks.metaldetector.web.api.request.RegisterUserRequest;
import rocks.metaldetector.web.api.request.ReleasesRequest;
import rocks.metaldetector.web.api.request.UpdateUserRequest;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.api.response.ArtistSearchResponseEntryDto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

public class DtoFactory {

  private static final String EXTERNAL_ID = "252211";
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

  public static class ReleaseRequestFactory {

    public static ReleasesRequest createDefault() {
      return ReleasesRequest.builder()
          .artists(List.of("A", "B", "C"))
          .dateFrom(LocalDate.now())
          .dateTo(LocalDate.now().plusDays(30))
          .build();
    }
  }

  public static class PaginatedReleaseRequestFactory {

    public static PaginatedReleasesRequest createDefault() {
      return PaginatedReleasesRequest.builder()
              .page(1)
              .size(10)
              .artists(List.of("A", "B", "C"))
              .dateFrom(LocalDate.now())
              .dateTo(LocalDate.now().plusDays(30))
              .build();
    }
  }

  public static class DiscogsArtistSearchResultDtoFactory {

    public static DiscogsArtistSearchResultDto createDefault() {
      Pagination pagination = Pagination.builder()
          .totalPages(1)
          .itemsPerPage(10)
          .currentPage(1)
          .build();

      return DiscogsArtistSearchResultDto.builder()
          .searchResults(List.of(DiscogsArtistSearchResultEntryDtoFactory.createDefault()))
          .pagination(pagination)
          .build();
    }

    public static DiscogsArtistSearchResultDto createMultiple() {
      Pagination pagination = Pagination.builder()
          .totalPages(10)
          .itemsPerPage(10)
          .currentPage(1)
          .build();

      return DiscogsArtistSearchResultDto.builder()
          .searchResults(List.of(DiscogsArtistSearchResultEntryDtoFactory.withArtistName("A"),
                                 DiscogsArtistSearchResultEntryDtoFactory.withArtistName("B")))
          .pagination(pagination)
          .build();
    }
  }

  public static class DiscogsArtistSearchResultEntryDtoFactory {

    static DiscogsArtistSearchResultEntryDto createDefault() {
      return withId(EXTERNAL_ID);
    }

    public static DiscogsArtistSearchResultEntryDto withId(String externalId) {
      return DiscogsArtistSearchResultEntryDto.builder()
          .id(externalId)
          .name(ARTIST_NAME)
          .build();
    }

    public static DiscogsArtistSearchResultEntryDto withArtistName(String artistName) {
      return DiscogsArtistSearchResultEntryDto.builder()
          .id("abcdef12345")
          .imageUrl("imageUrl")
          .name(artistName)
          .uri("/uri")
          .build();
    }
  }

  public static class DiscogsArtistDtoFactory {

    public static DiscogsArtistDto createDefault() {
      return DiscogsArtistDto.builder()
          .id(EXTERNAL_ID)
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

    public static ReleaseDto withReleaseDate(LocalDate releaseDate) {
      return ReleaseDto.builder()
          .artist("Evil Artist")
          .albumTitle("Heavy Release")
          .releaseDate(releaseDate)
          .build();
    }
  }

  public static class ImportJobResultDtoFactory {

    public static ImportJobResultDto createDefault() {
      return ImportJobResultDto.builder()
          .totalCountImported(666)
          .totalCountRequested(666)
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
          .externalId("1")
          .source("Discogs")
          .build();
    }
  }

  public static class SpotifyArtistSearchResultDtoFactory {

    public static SpotifyArtistSearchResultDto createDefault() {
      return SpotifyArtistSearchResultDto.builder()
          .pagination(new Pagination(1, 1, 10))
          .searchResults(List.of(
              SpotifyArtistDtoFactory.withArtistName("A"),
              SpotifyArtistDtoFactory.withArtistName("B")
          ))
          .build();
    }
  }

  public static class SpotifyArtistDtoFactory {

    public static SpotifyArtistDto createDefault() {
      return withArtistName("Slayer");
    }

    public static SpotifyArtistDto withArtistName(String artistName) {
      return SpotifyArtistDto.builder()
          .popularity(100)
          .genres(List.of("Black Metal"))
          .id("abcdef12345")
          .imageUrl("imageUrl")
          .name(artistName)
          .uri("uri")
          .follower(666)
          .build();
    }
  }

  public static class ArtistSearchResponseEntryDtoFactory {

    public static ArtistSearchResponseEntryDto spotifyWithArtistName(String artistName) {
      return ArtistSearchResponseEntryDto.builder()
          .popularity(100)
          .genres(List.of("Black Metal"))
          .id("abcdef12345")
          .imageUrl("imageUrl")
          .name(artistName)
          .uri("uri")
          .followed(false)
          .source(SPOTIFY.getDisplayName())
          .spotifyFollower(666)
          .build();
    }

    public static ArtistSearchResponseEntryDto discogsWithArtistName(String artistName) {
      return ArtistSearchResponseEntryDto.builder()
          .id("abcdef12345")
          .imageUrl("imageUrl")
          .name(artistName)
          .uri("http://discogs.com/uri")
          .followed(false)
          .source(DISCOGS.getDisplayName())
          .build();
    }

    public static ArtistSearchResponseEntryDto withId(String id) {
      return ArtistSearchResponseEntryDto.builder()
          .id(id)
          .build();
    }
  }

  public static class ArtistSearchResponseFactory {

    public static ArtistSearchResponse spotify() {
      return ArtistSearchResponse.builder()
          .pagination(new Pagination(1, 1, 10))
          .searchResults(List.of(ArtistSearchResponseEntryDtoFactory.spotifyWithArtistName("A")))
          .build();
    }

    public static ArtistSearchResponse discogs() {
      return ArtistSearchResponse.builder()
          .pagination(new Pagination(1, 1, 10))
          .searchResults(List.of(ArtistSearchResponseEntryDtoFactory.discogsWithArtistName("A")))
          .build();
    }

    public static ArtistSearchResponse empty() {
      return ArtistSearchResponse.builder()
          .searchResults(Collections.emptyList())
          .build();
    }
  }
}
