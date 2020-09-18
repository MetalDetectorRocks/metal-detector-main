package rocks.metaldetector.service.spotify;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.spotify.facade.SpotifyService;
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

  private final UserEntity user = UserEntityFactory.createUser("user", "user@mail.com");

  @BeforeEach
  void setup() {
    doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());
  }

  @AfterEach
  void tearDown() {
    reset(userRepository, currentPublicUserIdSupplier, spotifyService);
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
    assertThat(userEntity.getSpotifyAuthorizationEntity()).isNotNull();
    assertThat(userEntity.getSpotifyAuthorizationEntity().getState()).isNotNull();
    assertThat(userEntity.getSpotifyAuthorizationEntity().getState()).hasSize(STATE_SIZE);
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