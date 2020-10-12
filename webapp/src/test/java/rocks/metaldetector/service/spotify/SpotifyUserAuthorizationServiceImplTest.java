package rocks.metaldetector.service.spotify;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyUserAuthorizationDto;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static rocks.metaldetector.service.spotify.SpotifyUserAuthorizationServiceImpl.STATE_SIZE;

@ExtendWith(MockitoExtension.class)
class SpotifyUserAuthorizationServiceImplTest implements WithAssertions {

  @Mock
  private UserRepository userRepository;

  @Mock
  private CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @Mock
  private SpotifyService spotifyService;

  @InjectMocks
  private SpotifyUserAuthorizationServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(userRepository, currentPublicUserIdSupplier, spotifyService);
  }

  @Nested
  @DisplayName("Tests for authorization preparation")
  class PrepareAuthorizationTest {

    private final UserEntity user = UserEntityFactory.createUser("user", "user@mail.com");

    @BeforeEach
    void setup() {
      doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());
    }

    @Test
    @DisplayName("currentPublicUseridSupplier is called")
    void test_user_id_supplier_called() {
      // when
      underTest.prepareAuthorization();

      // then
      verify(currentPublicUserIdSupplier, times(1)).get();
    }

    @Test
    @DisplayName("userRepository is called to get current user")
    void test_user_repository_get_current_user() {
      // given
      var userId = "userId";
      doReturn(userId).when(currentPublicUserIdSupplier).get();

      // when
      underTest.prepareAuthorization();

      // then
      verify(userRepository, times(1)).findByPublicId(userId);
    }

    @Test
    @DisplayName("userRepository is called to save user with SpotifyAuthorizationEntity")
    void test_user_repository_save_user() {
      // given
      ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

      // when
      underTest.prepareAuthorization();

      // then
      verify(userRepository, times(1)).save(argumentCaptor.capture());

      UserEntity userEntity = argumentCaptor.getValue();
      assertThat(userEntity.getSpotifyAuthorization()).isNotNull();
      assertThat(userEntity.getSpotifyAuthorization().getState()).isNotNull();
      assertThat(userEntity.getSpotifyAuthorization().getState()).hasSize(STATE_SIZE);
    }

    @Test
    @DisplayName("userRepository is throws Exception if user is not found")
    void test_user_repository_throws_exception() {
      // given
      var userId = "userId";
      doThrow(new ResourceNotFoundException(userId)).when(userRepository).findByPublicId(any());

      // when
      var throwable = catchThrowable(() -> underTest.prepareAuthorization());

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(userId);
      verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("spotifyService is called to get authorization url")
    void test_spotify_service_called() {
      // when
      underTest.prepareAuthorization();

      // then
      verify(spotifyService, times(1)).getSpotifyAuthorizationUrl();
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
  @DisplayName("tests for fetching authorization token")
  class FetchTokenTest {

    private final UserEntity user = UserEntityFactory.createUser("user", "user@mail.com");
    private final SpotifyUserAuthorizationDto authorizationDto = SpotifyUserAuthorizationDto.builder().build();

    @BeforeEach
    void setup() {
      SpotifyAuthorizationEntity authorizationEntity = new SpotifyAuthorizationEntity("state");
      user.setSpotifyAuthorization(authorizationEntity);

      doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());
      doReturn(authorizationDto).when(spotifyService).getAccessToken(any());
    }

    @Test
    @DisplayName("currentPublicUseridSupplier is called")
    void test_user_id_supplier_called() {
      // when
      underTest.fetchToken("state", "code");

      // then
      verify(currentPublicUserIdSupplier, times(1)).get();
    }

    @Test
    @DisplayName("userRepository is called to get current user")
    void test_user_repository_get_current_user() {
      // given
      var userId = "userId";
      doReturn(userId).when(currentPublicUserIdSupplier).get();

      // when
      underTest.fetchToken("state", "code");

      // then
      verify(userRepository, times(1)).findByPublicId(userId);
    }

    @Test
    @DisplayName("Exception is thrown when authorization entity is null")
    void test_authorization_entity_null() {
      // given
      var userEntityWithoutAuth = UserEntityFactory.createUser("user", "user@mail.com");
      doReturn(Optional.of(userEntityWithoutAuth)).when(userRepository).findByPublicId(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.fetchToken("state", "code"));

      // then
      assertThat(throwable).isInstanceOf(IllegalStateException.class);
      assertThat(throwable).hasMessageContaining("snh:");
    }

    @Test
    @DisplayName("Exception is thrown when state is null")
    void test_state_null() {
      // given
      var userEntity = UserEntityFactory.createUser("user", "user@mail.com");
      var authorizationMock = Mockito.mock(SpotifyAuthorizationEntity.class);
      userEntity.setSpotifyAuthorization(authorizationMock);
      doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());
      doReturn(null).when(authorizationMock).getState();

      // when
      Throwable throwable = catchThrowable(() -> underTest.fetchToken("state", "code"));

      // then
      assertThat(throwable).isInstanceOf(IllegalStateException.class);
      assertThat(throwable).hasMessageContaining("snh:");
    }

    @Test
    @DisplayName("Exception is thrown when state does not match")
    void test_state_not_matching() {
      // given
      var userEntity = UserEntityFactory.createUser("user", "user@mail.com");
      var authorizationWrongState = new SpotifyAuthorizationEntity("unknownState");
      userEntity.setSpotifyAuthorization(authorizationWrongState);
      doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.fetchToken("state", "code"));

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
      underTest.fetchToken("state", code);

      // then
      verify(spotifyService, times(1)).getAccessToken(code);
    }

    @Test
    @DisplayName("userEntity is updated with token")
    void test_user_entity_updated() {
      // given
      var authorizationDto = SpotifyUserAuthorizationDto.builder()
          .accessToken("accessToken")
          .expiresIn(3600)
          .refreshToken("refreshToken")
          .scope("scope")
          .tokenType("tokenType")
          .build();
      doReturn(authorizationDto).when(spotifyService).getAccessToken(any());
      ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

      // when
      underTest.fetchToken("state", "code");

      // then
      verify(userRepository, times(1)).save(argumentCaptor.capture());
      SpotifyAuthorizationEntity updatedAuthorization = argumentCaptor.getValue().getSpotifyAuthorization();
      assertThat(updatedAuthorization.getAccessToken()).isEqualTo(authorizationDto.getAccessToken());
      assertThat(updatedAuthorization.getRefreshToken()).isEqualTo(authorizationDto.getRefreshToken());
      assertThat(updatedAuthorization.getExpiresIn()).isEqualTo(authorizationDto.getExpiresIn());
      assertThat(updatedAuthorization.getScope()).isEqualTo(authorizationDto.getScope());
      assertThat(updatedAuthorization.getTokenType()).isEqualTo(authorizationDto.getTokenType());
    }
  }
}
