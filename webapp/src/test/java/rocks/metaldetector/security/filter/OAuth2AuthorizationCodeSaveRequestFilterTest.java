package rocks.metaldetector.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateRepository;
import rocks.metaldetector.persistence.domain.user.RefreshTokenEntity;
import rocks.metaldetector.persistence.domain.user.RefreshTokenRepository;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.oauth.OAuth2AuthorizationCodeStateGenerator;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;
import static rocks.metaldetector.support.oauth.OAuth2ClientConfig.OAUTH_AUTHORIZATION_ENDPOINT;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthorizationCodeSaveRequestFilterTest implements WithAssertions {

  @InjectMocks
  private OAuth2AuthorizationCodeSaveRequestFilter underTest;

  @Mock
  private OAuth2AuthorizationCodeStateGenerator stateGenerator;

  @Mock
  private OAuthAuthorizationStateRepository authorizationStateRepository;

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @AfterEach
  void tearDown() {
    reset(stateGenerator, authorizationStateRepository, refreshTokenService, refreshTokenRepository);
  }

  @Test
  @DisplayName("Should filter if request URI does start with OAUTH_AUTHORIZATION_ENDPOINT")
  void test_should_filter() {
    // given
    var request = new MockHttpServletRequest();
    request.setRequestURI(OAUTH_AUTHORIZATION_ENDPOINT + "/some-registration-id");

    // when
    boolean result = underTest.shouldNotFilter(request);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Should not filter if request URI does not start with OAUTH_AUTHORIZATION_ENDPOINT")
  void test_should_not_filter() {
    // given
    var request = new MockHttpServletRequest();
    request.setRequestURI("/some/other/endpoint");

    // when
    boolean result = underTest.shouldNotFilter(request);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Should check if cookie is present and valid")
  void test_should_check_cookie() throws ServletException, IOException {
    // given
    var tokenValue = "some-token-value";
    var request = new MockHttpServletRequest();
    request.setCookies(new Cookie(REFRESH_TOKEN_COOKIE_NAME, tokenValue));

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verify(refreshTokenService).isValid(tokenValue);
  }

  @Test
  @DisplayName("Should send error with 403 if cookie is not present")
  void test_should_send_error() throws ServletException, IOException {
    // given
    var response = new MockHttpServletResponse();

    // when
    underTest.doFilterInternal(new MockHttpServletRequest(), response, new MockFilterChain());

    // then
    assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.value());
  }

  @Test
  @DisplayName("Should get token from database if cookie is valid")
  void test_should_get_token_from_database() throws ServletException, IOException {
    // given
    var tokenValue = "some-token-value";
    var request = new MockHttpServletRequest();
    request.setCookies(new Cookie(REFRESH_TOKEN_COOKIE_NAME, tokenValue));
    doReturn(true).when(refreshTokenService).isValid(anyString());

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verify(refreshTokenRepository).getByToken(tokenValue);
  }

  @Test
  @DisplayName("Should save authorization state if token is valid")
  void test_should_save_authorization_state() throws ServletException, IOException {
    // given
    ArgumentCaptor<OAuthAuthorizationStateEntity> stateEntityCaptor = ArgumentCaptor.forClass(OAuthAuthorizationStateEntity.class);
    var tokenValue = "some-token-value";
    var state = "some-state";
    var request = new MockHttpServletRequest();
    var user = UserEntityFactory.createDefaultUser();
    var refreshTokenEntity = new RefreshTokenEntity();
    refreshTokenEntity.setUser(user);
    request.setCookies(new Cookie(REFRESH_TOKEN_COOKIE_NAME, tokenValue));
    doReturn(true).when(refreshTokenService).isValid(anyString());
    doReturn(refreshTokenEntity).when(refreshTokenRepository).getByToken(tokenValue);
    doReturn(state).when(stateGenerator).generateState();

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verify(stateGenerator).generateState();
    verify(authorizationStateRepository).save(stateEntityCaptor.capture());
    var authorizationStateEntity = stateEntityCaptor.getValue();
    assertThat(authorizationStateEntity.getUser()).isEqualTo(user);
    assertThat(authorizationStateEntity.getState()).isEqualTo(state);
  }

  @Test
  @DisplayName("FilterChain is called")
  void test_filter_chain_is_called() throws ServletException, IOException {
    // given
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    var filterChain = new MockFilterChain();

    // when
    underTest.doFilterInternal(request, response, filterChain);

    // then
    assertThat(filterChain.getRequest()).isEqualTo(request);
    assertThat(filterChain.getResponse()).isEqualTo(response);
  }
}
