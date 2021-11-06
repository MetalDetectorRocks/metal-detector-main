package rocks.metaldetector.service.cleanup;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.token.TokenRepository;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.user.UserEntityFactory;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CleanupServiceImplTest implements WithAssertions {

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CleanupServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(tokenRepository, userRepository);
  }

  @Test
  @DisplayName("userRepository is called to find user with expired tokens")
  void test_user_repo_called_to_find_users() {
    // when
    underTest.cleanupUsersWithExpiredToken();

    // then
    verify(userRepository).findAllWithExpiredToken();
  }

  @Test
  @DisplayName("tokenRepository is called to delete expired tokens")
  void test_token_repository_deletes() {
    // given
    var users = List.of(UserEntityFactory.createUser("user", "user@user.user"));
    doReturn(users).when(userRepository).findAllWithExpiredToken();

    // when
    underTest.cleanupUsersWithExpiredToken();

    // then
    verify(tokenRepository).deleteAllByUserIn(users);
  }

  @Test
  @DisplayName("userRepository is called to delete users")
  void test_user_repository_deletes() {
    // given
    var users = List.of(UserEntityFactory.createUser("user", "user@user.user"));
    doReturn(users).when(userRepository).findAllWithExpiredToken();

    // when
    underTest.cleanupUsersWithExpiredToken();

    // then
    verify(userRepository).deleteAll(users);
  }

  @Test
  @DisplayName("nothing more is called of no users with expired tokens are present")
  void test_deletion_not_called_if_no_users_present() {
    // given
    doReturn(Collections.emptyList()).when(userRepository).findAllWithExpiredToken();

    // when
    underTest.cleanupUsersWithExpiredToken();

    // then
    verifyNoInteractions(tokenRepository);
    verifyNoMoreInteractions(userRepository);
  }
}
