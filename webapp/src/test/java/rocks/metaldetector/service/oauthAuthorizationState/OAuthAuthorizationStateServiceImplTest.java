package rocks.metaldetector.service.oauthAuthorizationState;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuthAuthorizationStateServiceImplTest implements WithAssertions {

  private static final UserEntity USER_ENTITY = UserEntityFactory.createDefaultUser();
  private static final OAuthAuthorizationStateEntity AUTHORIZATION_STATE_ENTITY = OAuthAuthorizationStateEntity.builder()
      .state("some_state")
      .user(USER_ENTITY)
      .build();

  @InjectMocks
  private OAuthAuthorizationStateServiceImpl underTest;

  @Mock
  private OAuthAuthorizationStateRepository authorizationStateRepository;

  @AfterEach
  void tearDown() {
    reset(authorizationStateRepository);
  }

  @Test
  @DisplayName("state repository is called with state to find user")
  void test_find_user_repo_called() {
    // given
    var state = "state";
    doReturn(Optional.of(AUTHORIZATION_STATE_ENTITY)).when(authorizationStateRepository).findByState(anyString());

    // when
    underTest.findUserByState(state);

    // then
    verify(authorizationStateRepository).findByState(state);
  }

  @Test
  @DisplayName("user is returned if state exists")
  void test_find_user_returns_user() {
    // given
    doReturn(Optional.of(AUTHORIZATION_STATE_ENTITY)).when(authorizationStateRepository).findByState(anyString());

    // when
    var result = underTest.findUserByState("state");

    // then
    assertThat(result).isEqualTo(USER_ENTITY);
  }

  @Test
  @DisplayName("exception is thrown if state does not exist")
  void test_find_user_throws_exception() {
    // given
    var state = "state";
    doReturn(Optional.empty()).when(authorizationStateRepository).findByState(anyString());

    // when / then
    assertThatThrownBy(() -> underTest.findUserByState(state))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found for state " + state);
  }

  @Test
  @DisplayName("state repository is called with state to delete state")
  void test_delete_by_state_repo_called() {
    // given
    var state = "state";

    // when
    underTest.deleteByState(state);

    // then
    verify(authorizationStateRepository).deleteByState(state);
  }
}
