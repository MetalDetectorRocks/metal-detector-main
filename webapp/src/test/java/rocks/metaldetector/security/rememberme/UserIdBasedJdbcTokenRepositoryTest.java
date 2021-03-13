package rocks.metaldetector.security.rememberme;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.security.rememberme.UserIdBasedJdbcTokenRepository.INSERT_TOKEN_SQL;
import static rocks.metaldetector.security.rememberme.UserIdBasedJdbcTokenRepository.REMOVE_USER_TOKENS_SQL;

@ExtendWith(MockitoExtension.class)
class UserIdBasedJdbcTokenRepositoryTest implements WithAssertions {

  private final UserIdBasedJdbcTokenRepository underTest = new UserIdBasedJdbcTokenRepository();

  @Mock
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setup() {
    underTest.setJdbcTemplate(jdbcTemplate);
  }

  @AfterEach
  void tearDown() {
    reset(jdbcTemplate);
  }

  @Test
  @DisplayName("jdbcTemplate is called with correct parameters on create")
  void test_create_token() {
    // given
    var token = new UserIdBasedPersistentRememberMeToken(1L, "username", "series", "token", new Date());

    // when
    underTest.createNewToken(token);

    // then
    verify(jdbcTemplate).update(INSERT_TOKEN_SQL, token.getUserId(), token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate());
  }

  @Test
  @DisplayName("jdbcTemplate is called with correct parameters on removeById")
  void test_remove_tokens_by_id() {
    // given
    var userId = 1L;

    // when
    underTest.removeUserTokensByUserId(userId);

    // then
    verify(jdbcTemplate).update(REMOVE_USER_TOKENS_SQL, userId);
  }

  @Test
  @DisplayName("Exception is thrown on removeTokens")
  void test_exception_on_remove_tokens() {
    // when
    var throwable = catchThrowable(() -> underTest.removeUserTokens("username"));

    // then
    assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
  }
}
