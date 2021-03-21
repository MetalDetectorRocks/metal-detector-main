package rocks.metaldetector.service.user.events;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.AccountDeletedEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.service.user.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.user.events.UserDeletionEventListener.DELETE_QUERY;
import static rocks.metaldetector.service.user.events.UserDeletionEventListener.PARAMETER_NAME;
import static rocks.metaldetector.service.user.events.UserDeletionEventListener.VARCHAR_SQL_TYPE;

@ExtendWith(MockitoExtension.class)
class UserDeletionEventListenerTest implements WithAssertions {

  @Mock
  private FollowActionRepository followActionRepository;

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @Mock
  private SpotifyAuthorizationRepository spotifyAuthorizationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private UserDeletionEventListener underTest;

  private UserDeletionEvent userDeletionEvent;

  @BeforeEach
  void setup() {
    UserEntity userEntity = UserEntityFactory.createUser("userName", "user@mail.com");
    UserService userService = Mockito.mock(UserService.class);
    userDeletionEvent = new UserDeletionEvent(userService, userEntity);
  }

  @AfterEach
  void tearDown() {
    reset(followActionRepository, notificationConfigRepository, spotifyAuthorizationRepository, userRepository, jdbcTemplate, emailService);
  }

  @Test
  @DisplayName("notificationConfig is deleted")
  void test_notification_config_deleted() {
    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(notificationConfigRepository).deleteByUserId(userDeletionEvent.getUserEntity().getId());
  }

  @Test
  @DisplayName("SpotifyAuthorization is fetched from repository")
  void test_spotify_authorization_fetched() {
    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(spotifyAuthorizationRepository).findByUserId(userDeletionEvent.getUserEntity().getId());
  }

  @Test
  @DisplayName("If present SpotifyAuthorization is deleted")
  void test_spotify_authorization_deleted() {
    // given
    SpotifyAuthorizationEntity spotifyAuthorization = SpotifyAuthorizationEntity.builder().user(userDeletionEvent.getUserEntity()).build();
    doReturn(Optional.of(spotifyAuthorization)).when(spotifyAuthorizationRepository).findByUserId(any());

    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(spotifyAuthorizationRepository).delete(spotifyAuthorization);
  }

  @Test
  @DisplayName("FollowActions are deleted")
  void test_follow_actions_deleted() {
    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(followActionRepository).deleteAllByUser(userDeletionEvent.getUserEntity());
  }

  @Test
  @DisplayName("Persistent logins are clear via jdbcTemplate")
  void test_persistent_logins_cleared() {
    // given
    ArgumentCaptor<MapSqlParameterSource> argumentCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);

    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(jdbcTemplate).update(eq(DELETE_QUERY), argumentCaptor.capture());
    MapSqlParameterSource parameterSource = argumentCaptor.getValue();
    SqlParameterValue value = (SqlParameterValue) parameterSource.getValue(PARAMETER_NAME);

    assertThat(value).isNotNull();
    assertThat(value.getSqlType()).isEqualTo(VARCHAR_SQL_TYPE);
    assertThat((String) value.getValue()).isEqualTo(userDeletionEvent.getUserEntity().getMetalDetectorUsername());
  }

  @Test
  @DisplayName("Email is sent on deletion")
  void test_email_is_sent() {
    // given
    ArgumentCaptor<AccountDeletedEmail> argumentCaptor = ArgumentCaptor.forClass(AccountDeletedEmail.class);

    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(emailService).sendEmail(argumentCaptor.capture());
    AccountDeletedEmail email = argumentCaptor.getValue();

    assertThat(email.getRecipient()).isEqualTo(userDeletionEvent.getUserEntity().getEmail());
    assertThat(email.getTemplateName()).isEqualTo(ViewNames.EmailTemplates.ACCOUNT_DELETED);
    assertThat(email.getSubject()).isEqualTo(AccountDeletedEmail.SUBJECT);
  }

  @Test
  @DisplayName("UserEntity is deleted")
  void test_user_deleted() {
    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(userRepository).delete(userDeletionEvent.getUserEntity());
  }
}