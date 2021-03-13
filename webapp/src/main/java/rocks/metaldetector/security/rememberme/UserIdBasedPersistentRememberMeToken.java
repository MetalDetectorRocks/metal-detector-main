package rocks.metaldetector.security.rememberme;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.util.Date;

@Getter
@Setter
public class UserIdBasedPersistentRememberMeToken extends PersistentRememberMeToken {

  private final long userId;

  public UserIdBasedPersistentRememberMeToken(long userId, String username, String series, String tokenValue, Date date) {
    super(username, series, tokenValue, date);
    this.userId = userId;
  }
}
