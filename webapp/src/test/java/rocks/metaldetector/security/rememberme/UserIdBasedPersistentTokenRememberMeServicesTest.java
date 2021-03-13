package rocks.metaldetector.security.rememberme;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserIdBasedPersistentTokenRememberMeServicesTest implements WithAssertions {

  private UserIdBasedPersistentTokenRememberMeServices underTest;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private UserIdBasedJdbcTokenRepository tokenRepository;

  @BeforeEach
  void setup() {
    underTest = new UserIdBasedPersistentTokenRememberMeServices("key", userDetailsService, tokenRepository);
  }

  @AfterEach
  void tearDown() {
    reset(userDetailsService, tokenRepository);
  }

  @Test
  @DisplayName("tokenRepository is called on loginSuccess")
  void test_token_repository_called_on_login_success() {
    // given
    ArgumentCaptor<UserIdBasedPersistentRememberMeToken> argumentCaptor = ArgumentCaptor.forClass(UserIdBasedPersistentRememberMeToken.class);
    var authentication = mock(Authentication.class);
    var user = mock(UserEntity.class);
    doReturn(user).when(authentication).getPrincipal();
    doReturn("username").when(user).getUsername();
    doReturn(1L).when(user).getId();

    // when
    underTest.onLoginSuccess(new MockHttpServletRequest(), new MockHttpServletResponse(), authentication);

    // then
    verify(tokenRepository).createNewToken(argumentCaptor.capture());

    var token = argumentCaptor.getValue();
    assertThat(token.getUserId()).isEqualTo(user.getId());
    assertThat(token.getUsername()).isEqualTo(user.getUsername());
    assertThat(token.getSeries()).isNotBlank();
    assertThat(token.getTokenValue()).isNotBlank();
    assertThat(token.getDate()).isCloseTo(new Date(), 1000L);
  }

  @Test
  @DisplayName("All tokens for user are deleted if they don't match")
  void test_tokens_deleted_if_not_matching() {
    // given
    var cookieTokens = new String[] {"series", "token"};
    var notMatchingToken = new UserIdBasedPersistentRememberMeToken(1L, "username", "otherSeries", "otherToken", new Date());
    doReturn(notMatchingToken).when(tokenRepository).getTokenForSeries(any());

    // when
    var throwable = catchThrowable(() -> underTest.processAutoLoginCookie(cookieTokens, new MockHttpServletRequest(), new MockHttpServletResponse()));

    // then
    verify(tokenRepository).removeUserTokensByUserId(notMatchingToken.getUserId());
    assertThat(throwable).isInstanceOf(CookieTheftException.class);
  }

  @Test
  @DisplayName("On successful login token is updated with same series value")
  void test_token_is_updated() {
    // given
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    var cookieTokens = new String[] {"series", "token"};
    var token = new UserIdBasedPersistentRememberMeToken(1L, "username", "series", "token", new Date());
    doReturn(token).when(tokenRepository).getTokenForSeries(any());

    // when
    underTest.processAutoLoginCookie(cookieTokens, new MockHttpServletRequest(), new MockHttpServletResponse());

    // then
    verify(tokenRepository).updateToken(argumentCaptor.capture(), argumentCaptor.capture(), any());

    var arguments = argumentCaptor.getAllValues();
    assertThat(arguments.size()).isEqualTo(2);
    assertThat(arguments.get(0)).isEqualTo(token.getSeries());
    assertThat(arguments.get(1)).isNotEqualTo(token.getTokenValue());
  }

  @Test
  @DisplayName("On logout all tokens for userId are deleted")
  void test_tokens_are_deleted() {
    // given
    var authentication = mock(Authentication.class);
    var user = mock(UserEntity.class);
    doReturn(user).when(authentication).getPrincipal();
    doReturn(1L).when(user).getId();

    // when
    underTest.logout(new MockHttpServletRequest(), new MockHttpServletResponse(), authentication);

    // then
    verify(tokenRepository).removeUserTokensByUserId(user.getId());
  }
}