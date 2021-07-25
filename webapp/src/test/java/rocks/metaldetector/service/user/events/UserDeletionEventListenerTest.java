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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.AccountDeletedEmail;
import rocks.metaldetector.service.email.EmailService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.service.user.UserService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.user.events.UserDeletionEventListener.DELETE_QUERY;
import static rocks.metaldetector.service.user.events.UserDeletionEventListener.PARAMETER_NAME;
import static rocks.metaldetector.service.user.events.UserDeletionEventListener.SPOTIFY_REGISTRATION_ID;
import static rocks.metaldetector.service.user.events.UserDeletionEventListener.VARCHAR_SQL_TYPE;

@ExtendWith(MockitoExtension.class)
class UserDeletionEventListenerTest implements WithAssertions {

  @Mock
  private FollowActionRepository followActionRepository;

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @Mock
  private TelegramConfigRepository telegramConfigRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Mock
  private EmailService emailService;

  @Mock
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

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
    reset(followActionRepository, notificationConfigRepository, userRepository,
          jdbcTemplate, emailService, telegramConfigRepository, oAuth2AuthorizedClientService);
  }

  @Test
  @DisplayName("notificationConfigs are deleted")
  void test_notification_configs_deleted() {
    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(notificationConfigRepository).deleteAllByUser(userDeletionEvent.getUserEntity());
  }

  @Test
  @DisplayName("telegramConfig is deleted")
  void test_telegram_config_deleted() {
    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(telegramConfigRepository).deleteByUser(userDeletionEvent.getUserEntity());
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
  @DisplayName("spotify oAuth token is deleted")
  void test_spotify_oauth_token_deleted() {
    // given

    // when
    underTest.onApplicationEvent(userDeletionEvent);

    // then
    verify(oAuth2AuthorizedClientService).removeAuthorizedClient(SPOTIFY_REGISTRATION_ID, userDeletionEvent.getUserEntity().getUsername());
  }

  @Test
  @DisplayName("Persistent logins are cleared via jdbcTemplate")
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
    assertThat((String) value.getValue()).isEqualTo(userDeletionEvent.getUserEntity().getUsername());
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
