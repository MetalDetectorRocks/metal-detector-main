package rocks.metaldetector.service.auth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
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
class RegistrationCleanupServiceTest implements WithAssertions {

  @Mock
  private UserRepository userRepository;

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @InjectMocks
  private RegistrationCleanupService underTest;

  @AfterEach
  void tearDown() {
    reset(userRepository, notificationConfigRepository);
  }

  @Test
  @DisplayName("userRepository is called to find user with expired tokens")
  void test_user_repo_called_to_find_users() {
    // when
    underTest.cleanupUsersWithExpiredToken();

    // then
    verify(userRepository).findAllExpiredUsers();
  }

  @Test
  @DisplayName("notificationConfigRepository is called to delete notification configs")
  void test_notification_config_repository_deletes() {
    // given
    var users = List.of(UserEntityFactory.createUser("user", "user@user.user"));
    doReturn(users).when(userRepository).findAllExpiredUsers();

    // when
    underTest.cleanupUsersWithExpiredToken();

    // then
    verify(notificationConfigRepository).deleteAllByUserIn(List.of(users.get(0)));
  }

  @Test
  @DisplayName("userRepository is called to delete users")
  void test_user_repository_deletes() {
    // given
    var users = List.of(UserEntityFactory.createUser("user", "user@user.user"));
    doReturn(users).when(userRepository).findAllExpiredUsers();

    // when
    underTest.cleanupUsersWithExpiredToken();

    // then
    verify(userRepository).deleteAll(users);
  }

  @Test
  @DisplayName("nothing more is called of no users with expired tokens are present")
  void test_deletion_not_called_if_no_users_present() {
    // given
    doReturn(Collections.emptyList()).when(userRepository).findAllExpiredUsers();

    // when
    underTest.cleanupUsersWithExpiredToken();

    // then
    verifyNoInteractions(notificationConfigRepository);
    verifyNoMoreInteractions(userRepository);
  }
}
