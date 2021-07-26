package rocks.metaldetector.service.user.events;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.AccountDeletedEmail;
import rocks.metaldetector.service.email.EmailService;

@Slf4j
@AllArgsConstructor
@Component
public class UserDeletionEventListener implements ApplicationListener<UserDeletionEvent> {

  static final String DELETE_QUERY = "delete from persistent_logins where username = :userName";
  static final String PARAMETER_NAME = "userName";
  static final int VARCHAR_SQL_TYPE = 12;
  static final String SPOTIFY_REGISTRATION_ID = "spotify-user";

  private final FollowActionRepository followActionRepository;
  private final NotificationConfigRepository notificationConfigRepository;
  private final TelegramConfigRepository telegramConfigRepository;
  private final UserRepository userRepository;
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final EmailService emailService;
  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @Override
  @Transactional
  public void onApplicationEvent(UserDeletionEvent event) {
    AbstractUserEntity user = event.getUserEntity();
    log.info("User '" + user.getPublicId() + "' deleted");

    telegramConfigRepository.deleteByUser(user);
    notificationConfigRepository.deleteAllByUser(user);
    followActionRepository.deleteAllByUser(user);

    if (user instanceof UserEntity) {
      clearPersistentLogins(user.getUsername());
    }

    AccountDeletedEmail email = new AccountDeletedEmail(user.getEmail(), user.getUsername());
    emailService.sendEmail(email);

    oAuth2AuthorizedClientService.removeAuthorizedClient(SPOTIFY_REGISTRATION_ID, user.getUsername());

    userRepository.delete(user);
  }

  private void clearPersistentLogins(String userName) {
    SqlParameterSource parameters = new MapSqlParameterSource(PARAMETER_NAME, new SqlParameterValue(VARCHAR_SQL_TYPE, userName));
    jdbcTemplate.update(DELETE_QUERY, parameters);
  }
}
