package rocks.metaldetector.service.user.events;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.email.AccountDeletedEmail;
import rocks.metaldetector.service.email.EmailService;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class UserDeletionEventListener implements ApplicationListener<UserDeletionEvent> {

  static final String DELETE_QUERY = "delete from persistent_logins where username = :userName";
  static final String PARAMETER_NAME = "userName";
  static final int VARCHAR_SQL_TYPE = 12;

  private final FollowActionRepository followActionRepository;
  private final NotificationConfigRepository notificationConfigRepository;
  private final SpotifyAuthorizationRepository spotifyAuthorizationRepository;
  private final UserRepository userRepository;
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final EmailService emailService;

  @Override
  @Transactional
  public void onApplicationEvent(UserDeletionEvent event) {
    AbstractUserEntity user = event.getUserEntity();
    log.info("User '" + user.getPublicId() + "' deleted");

    Optional<SpotifyAuthorizationEntity> spotifyAuthorizationOptional = spotifyAuthorizationRepository.findByUserId(user.getId());
    spotifyAuthorizationOptional.ifPresent(spotifyAuthorizationRepository::delete);
    notificationConfigRepository.deleteByUserId(user.getId());
    followActionRepository.deleteAllByUser(user);
    clearPersistentLogins(user.getUsername());

    AccountDeletedEmail email = new AccountDeletedEmail(user.getEmail(), user.getUsername());
    emailService.sendEmail(email);

    userRepository.delete(user);
  }

  private void clearPersistentLogins(String userName) {
    SqlParameterSource parameters = new MapSqlParameterSource(PARAMETER_NAME, new SqlParameterValue(VARCHAR_SQL_TYPE, userName));
    jdbcTemplate.update(DELETE_QUERY, parameters);
  }
}
