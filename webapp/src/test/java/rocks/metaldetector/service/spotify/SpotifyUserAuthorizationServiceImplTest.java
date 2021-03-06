package rocks.metaldetector.service.spotify;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyUserAuthorizationDto;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.service.spotify.SpotifyUserAuthorizationServiceImpl.GRACE_PERIOD_SECONDS;
import static rocks.metaldetector.service.spotify.SpotifyUserAuthorizationServiceImpl.STATE_SIZE;

@ExtendWith(MockitoExtension.class)
class SpotifyUserAuthorizationServiceImplTest implements WithAssertions {

  @Mock
  private SpotifyAuthorizationRepository authorizationRepository;

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private SpotifyService spotifyService;

  @Mock
  private UserEntity userMock;

  private SpotifyUserAuthorizationServiceImpl underTest;

  @BeforeEach
  void setup() {
    underTest = new SpotifyUserAuthorizationServiceImpl(currentUserSupplier, authorizationRepository, spotifyService);
    doReturn(userMock).when(currentUserSupplier).get();
  }

  @AfterEach
  void tearDown() {
    reset(authorizationRepository, currentUserSupplier, spotifyService);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for authorization existence check")
  class ExistAuthorizationTest {

    @Test
    @DisplayName("should call current user supplier")
    void should_call_current_user_supplier() {
      // when
      underTest.exists();

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("should call authorization repository with user id")
    void should_call_authorization_repository_with_user_id() {
      // when
      underTest.exists();

      // then
      verify(authorizationRepository).findByUser(userMock);
    }

    @Test
    @DisplayName("should return true if access token and refresh token exist")
    void should_return_true() {
      // given
      SpotifyAuthorizationEntity authorizationEntity = SpotifyAuthorizationEntity.builder().user(userMock).accessToken("foo").refreshToken("bar").build();
      doReturn(Optional.of(authorizationEntity)).when(authorizationRepository).findByUser(any());

      // when
      boolean result = underTest.exists();

      // then
      assertThat(result).isTrue();
    }

    @ParameterizedTest(name = "should return false for <{0}>")
    @MethodSource("incompleteAuthorizationEntityProvider")
    @DisplayName("should return false if access token or refresh token don't exist")
    void should_return_false(SpotifyAuthorizationEntity authorizationEntity) {
      // given
      doReturn(Optional.of(authorizationEntity)).when(authorizationRepository).findByUser(any());

      // when
      boolean result = underTest.exists();

      // then
      assertThat(result).isFalse();
    }

    private Stream<Arguments> incompleteAuthorizationEntityProvider() {
      UserEntity user = UserEntityFactory.createUser("user", "user@example.com");
      return Stream.of(
          Arguments.of(SpotifyAuthorizationEntity.builder().user(user).build()),
          Arguments.of(SpotifyAuthorizationEntity.builder().user(user).refreshToken("foo").build()),
          Arguments.of(SpotifyAuthorizationEntity.builder().user(user).accessToken("foo").build())
      );
    }
  }

  @Nested
  @DisplayName("Tests for authorization preparation")
  class PrepareAuthorizationTest {

    @Test
    @DisplayName("currentUserSupplier is called")
    void test_user_id_supplier_called() {
      // when
      underTest.prepareAuthorization();

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("spotifyUserAuthorizationRepository is called")
    void spotify_user_authorization_repository_is_called() {
      // given
      doReturn(userMock).when(currentUserSupplier).get();

      // when
      underTest.prepareAuthorization();

      // then
      verify(authorizationRepository).findByUser(userMock);
    }

    @Test
    @DisplayName("spotifyAuthorizationRepository is called to save authorization entity if no entity already exists")
    void test_authorization_repository_is_called() {
      // given
      doReturn(Optional.empty()).when(authorizationRepository).findByUser(any());
      ArgumentCaptor<SpotifyAuthorizationEntity> argumentCaptor = ArgumentCaptor.forClass(SpotifyAuthorizationEntity.class);

      // when
      underTest.prepareAuthorization();

      // then
      verify(authorizationRepository).save(argumentCaptor.capture());

      SpotifyAuthorizationEntity authorizationEntity = argumentCaptor.getValue();
      assertThat(authorizationEntity.getState()).isNotNull();
      assertThat(authorizationEntity.getState()).hasSize(STATE_SIZE);
      assertThat(authorizationEntity.getUser()).isEqualTo(userMock);
    }

    @Test
    @DisplayName("state from existing authorization entity is used")
    void test_state_from_existing_authorization_entity_is_used() {
      // given
      var existingState = "spotify-state";
      SpotifyAuthorizationEntity authorizationMock = mock(SpotifyAuthorizationEntity.class);
      doReturn(existingState).when(authorizationMock).getState();
      doReturn(Optional.of(authorizationMock)).when(authorizationRepository).findByUser(any());

      // when
      var result = underTest.prepareAuthorization();

      // then
      verify(authorizationMock).getState();
      assertThat(result).endsWith(existingState);
    }

    @Test
    @DisplayName("spotifyService is called to get authorization url")
    void test_spotify_service_called() {
      // when
      underTest.prepareAuthorization();

      // then
      verify(spotifyService).getSpotifyAuthorizationUrl();
    }

    @Test
    @DisplayName("authorization url with state is returned")
    void test_authorization_url_returned() {
      // given
      var url = "url";
      doReturn(url).when(spotifyService).getSpotifyAuthorizationUrl();

      // when
      var result = underTest.prepareAuthorization();

      // then
      assertThat(result).startsWith(url);
      assertThat(result).hasSize(url.length() + STATE_SIZE);
    }
  }

  @Nested
  @DisplayName("Tests for persisting authorization token")
  class PersistTokenTest {

    private static final String SAMPLE_STATE = "sample-state";
    private final SpotifyUserAuthorizationDto authorizationDto = SpotifyUserAuthorizationDto.builder().build();

    @BeforeEach
    void setup() {
      SpotifyAuthorizationEntity authorizationEntity = SpotifyAuthorizationEntity.builder()
          .user(userMock)
          .state(SAMPLE_STATE)
          .accessToken("foo")
          .refreshToken("bar")
          .build();
      doReturn(Optional.of(authorizationEntity)).when(authorizationRepository).findByUser(any());
      doReturn(authorizationDto).when(spotifyService).getAccessToken(any());
    }

    @Test
    @DisplayName("currentUserSupplier is called")
    void test_user_id_supplier_called() {
      // when
      underTest.persistInitialToken(SAMPLE_STATE, "code");

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("should call spotify authorization repository with user id")
    void should_call_spotify_authorization_repository_with_user_id() {
      // when
      underTest.persistInitialToken(SAMPLE_STATE, "code");

      // then
      verify(authorizationRepository).findByUser(userMock);
    }

    @Test
    @DisplayName("Exception is thrown when authorization entity is empty")
    void test_authorization_entity_null() {
      // given
      doReturn(Optional.empty()).when(authorizationRepository).findByUser(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.persistInitialToken(SAMPLE_STATE, "code"));

      // then
      assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Exception is thrown when state is null")
    void test_state_null() {
      // given
      var authorizationMock = mock(SpotifyAuthorizationEntity.class);
      doReturn(null).when(authorizationMock).getState();
      doReturn(Optional.of(authorizationMock)).when(authorizationRepository).findByUser(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.persistInitialToken(SAMPLE_STATE, "code"));

      // then
      assertThat(throwable).isInstanceOf(IllegalStateException.class);
      assertThat(throwable).hasMessageContaining("snh:");
    }

    @Test
    @DisplayName("Exception is thrown when state does not match")
    void test_state_not_matching() {
      // given
      var authorizationMock = mock(SpotifyAuthorizationEntity.class);
      doReturn("unknown-state").when(authorizationMock).getState();
      doReturn(Optional.of(authorizationMock)).when(authorizationRepository).findByUser(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.persistInitialToken(SAMPLE_STATE, "code"));

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
      assertThat(throwable).hasMessageContaining("state validation failed");
    }

    @Test
    @DisplayName("spotifyService is called to get access token")
    void test_spotify_Service_called() {
      // given
      var code = "code";

      // when
      underTest.persistInitialToken(SAMPLE_STATE, code);

      // then
      verify(spotifyService).getAccessToken(code);
    }

    @Test
    @DisplayName("authorization entity is updated with token")
    void test_authorization_entity_updated() {
      // given
      var authorizationDto = SpotifyUserAuthorizationDto.builder()
          .accessToken("accessToken")
          .expiresIn(3600)
          .refreshToken("refreshToken")
          .scope("scope")
          .tokenType("tokenType")
          .build();
      doReturn(authorizationDto).when(spotifyService).getAccessToken(any());
      ArgumentCaptor<SpotifyAuthorizationEntity> argumentCaptor = ArgumentCaptor.forClass(SpotifyAuthorizationEntity.class);
      var now = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

      // when
      try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class)) {
        mock.when(LocalDateTime::now).thenReturn(now);
        underTest.persistInitialToken(SAMPLE_STATE, "code");
      }

      // then
      verify(authorizationRepository).save(argumentCaptor.capture());
      SpotifyAuthorizationEntity updatedAuthorization = argumentCaptor.getValue();
      assertThat(updatedAuthorization.getAccessToken()).isEqualTo(authorizationDto.getAccessToken());
      assertThat(updatedAuthorization.getRefreshToken()).isEqualTo(authorizationDto.getRefreshToken());
      assertThat(updatedAuthorization.getExpiresAt()).isEqualTo(now.plusSeconds(authorizationDto.getExpiresIn() - GRACE_PERIOD_SECONDS));
      assertThat(updatedAuthorization.getScope()).isEqualTo(authorizationDto.getScope());
      assertThat(updatedAuthorization.getTokenType()).isEqualTo(authorizationDto.getTokenType());
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for getting and if necessary refreshing the token")
  class GetOrRefreshTokenTest {

    private SpotifyAuthorizationEntity authorizationEntity;

    @BeforeEach
    void setup() {
      authorizationEntity = SpotifyAuthorizationEntity.builder()
          .user(userMock)
          .state("sample-state")
          .accessToken("foo")
          .refreshToken("bar")
          .expiresAt(LocalDateTime.of(2100, 1, 1, 0, 0))
          .build();
      authorizationEntity.setCreatedDateTime(Date.from(Instant.now()));
      doReturn(Optional.of(authorizationEntity)).when(authorizationRepository).findByUser(any());
    }

    @Test
    @DisplayName("currentUserSupplier is called")
    void test_user_id_supplier_called() {
      // when
      underTest.getOrRefreshToken();

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("should call spotify authorization repository with user id")
    void should_call_spotify_authorization_repository_with_user_id() {
      // when
      underTest.getOrRefreshToken();

      // then
      verify(authorizationRepository).findByUser(userMock);
    }

    @Test
    @DisplayName("Exception is thrown when authorization entity is empty")
    void test_authorization_entity_null() {
      // given
      doReturn(Optional.empty()).when(authorizationRepository).findByUser(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.getOrRefreshToken());

      // then
      assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "exception is thrown when authorization entity is {0}")
    @MethodSource("incompleteAuthorizationEntityProvider")
    @DisplayName("exception is thrown when authorization entity is incomplete")
    void test_incomplete_authorization_entity(SpotifyAuthorizationEntity authorizationEntity) {
      // given
      doReturn(Optional.of(authorizationEntity)).when(authorizationRepository).findByUser(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.getOrRefreshToken());

      // then
      assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("accessToken is returned if it is still valid")
    void test_access_token_returned() {
      // when
      var result = underTest.getOrRefreshToken();

      // then
      assertThat(result).isEqualTo(authorizationEntity.getAccessToken());
    }

    @Test
    @DisplayName("spotifyService is not called if accessToken is still valid")
    void test_spotify_service_not_called() {
      // when
      underTest.getOrRefreshToken();

      // then
      verifyNoInteractions(spotifyService);
    }

    @Test
    @DisplayName("spotifyService is called with refreshToken if accessToken is expired")
    void test_spotify_service_called() {
      // given
      var spotifyAuthorization = SpotifyAuthorizationEntity.builder()
          .user(userMock).expiresAt(LocalDateTime.now()).refreshToken("refreshToken").accessToken("accessToken").build();
      spotifyAuthorization.setCreatedDateTime(Date.from(Instant.now().minus(120, SECONDS)));
      doReturn(Optional.of(spotifyAuthorization)).when(authorizationRepository).findByUser(any());

      var authorizationDto = SpotifyUserAuthorizationDto.builder().build();
      doReturn(authorizationDto).when(spotifyService).refreshToken(any());

      // when
      underTest.getOrRefreshToken();

      // then
      verify(spotifyService).refreshToken(spotifyAuthorization.getRefreshToken());
    }

    @Test
    @DisplayName("authorizationEntity is updated with new values after refreshing")
    void test_authorization_entity_updated() {
      // given
      ArgumentCaptor<SpotifyAuthorizationEntity> argumentCaptor = ArgumentCaptor.forClass(SpotifyAuthorizationEntity.class);
      var spotifyAuthorization = SpotifyAuthorizationEntity.builder()
          .user(userMock).expiresAt(LocalDateTime.of(2019, 1, 1, 0, 0, 0)).refreshToken("refreshToken").accessToken("accessToken").build();
      spotifyAuthorization.setCreatedDateTime(Date.from(Instant.now().minus(120, SECONDS)));
      doReturn(Optional.of(spotifyAuthorization)).when(authorizationRepository).findByUser(any());

      var authorizationDto = SpotifyUserAuthorizationDto.builder()
          .accessToken("newAccessToken").expiresIn(120).tokenType("newTokenTyp").scope("newScope").build();
      doReturn(authorizationDto).when(spotifyService).refreshToken(any());
      var now = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

      // when
      try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class)) {
        mock.when(LocalDateTime::now).thenReturn(now);
        underTest.getOrRefreshToken();
      }

      // then
      verify(authorizationRepository).save(argumentCaptor.capture());
      var updatedAuthorizationEntity = argumentCaptor.getValue();
      assertThat(updatedAuthorizationEntity.getAccessToken()).isEqualTo(authorizationDto.getAccessToken());
      assertThat(updatedAuthorizationEntity.getScope()).isEqualTo(authorizationDto.getScope());
      assertThat(updatedAuthorizationEntity.getExpiresAt()).isEqualTo(now.plusSeconds(authorizationDto.getExpiresIn() - GRACE_PERIOD_SECONDS));
      assertThat(updatedAuthorizationEntity.getTokenType()).isEqualTo(authorizationDto.getTokenType());
    }

    @Test
    @DisplayName("new accessToken is returned after refreshing")
    void test_new_token_returned() {
      // given
      var spotifyAuthorization = SpotifyAuthorizationEntity.builder()
          .user(userMock).expiresAt(LocalDateTime.now()).refreshToken("refreshToken").accessToken("accessToken").build();
      spotifyAuthorization.setCreatedDateTime(Date.from(Instant.now().minus(120, SECONDS)));
      doReturn(Optional.of(spotifyAuthorization)).when(authorizationRepository).findByUser(any());

      var authorizationDto = SpotifyUserAuthorizationDto.builder().accessToken("newAccessToken").build();
      doReturn(authorizationDto).when(spotifyService).refreshToken(any());

      // when
      var result = underTest.getOrRefreshToken();

      // then
      assertThat(result).isEqualTo(authorizationDto.getAccessToken());
    }

    private Stream<Arguments> incompleteAuthorizationEntityProvider() {
      UserEntity user = UserEntityFactory.createUser("user", "user@example.com");
      return Stream.of(
          Arguments.of(SpotifyAuthorizationEntity.builder().user(user).build()),
          Arguments.of(SpotifyAuthorizationEntity.builder().user(user).refreshToken("").build())
      );
    }
  }

  @Nested
  @DisplayName("Tests for deleting spotify authorization")
  class DeleteSpotifyAuthorizationTest {

    @Test
    @DisplayName("currentUserSupplier is called on deletion")
    void test_current_user_supplier_called() {
      // when
      underTest.deleteAuthorization();

      // then
      verify(currentUserSupplier).get();
    }

    @Test
    @DisplayName("spotifyAuthorizationRepository is called to delete user's entity")
    void test_spotify_repository_called() {
      // when
      underTest.deleteAuthorization();

      // then
      verify(authorizationRepository).deleteByUser(userMock);
    }
  }
}
