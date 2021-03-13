package rocks.metaldetector.security.rememberme;

import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.util.Objects;

public class UserIdBasedJdbcTokenRepository extends JdbcTokenRepositoryImpl {

  static final String INSERT_TOKEN_SQL = "insert into persistent_logins (user_id, username, series, token, last_used) values(?,?,?,?,?)";
  static final String REMOVE_USER_TOKENS_SQL = "delete from persistent_logins where user_id = ?";

  @Override
  public void createNewToken(PersistentRememberMeToken token) {
    Objects.requireNonNull(getJdbcTemplate()).update(INSERT_TOKEN_SQL, ((UserIdBasedPersistentRememberMeToken) token).getUserId(), token.getUsername(),
                                                     token.getSeries(), token.getTokenValue(), token.getDate());
  }

  @Override
  public void removeUserTokens(String username) {
    throw new UnsupportedOperationException("removeUserTokensByUserId() has to be called");
  }

  public void removeUserTokensByUserId(long userId) {
    Objects.requireNonNull(getJdbcTemplate()).update(REMOVE_USER_TOKENS_SQL, userId);
  }
}
