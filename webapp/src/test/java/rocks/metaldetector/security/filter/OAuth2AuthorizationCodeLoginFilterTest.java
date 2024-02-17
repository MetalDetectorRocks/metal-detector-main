package rocks.metaldetector.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import rocks.metaldetector.service.oauthAuthorizationState.OAuthAuthorizationStateService;

import static org.mockito.Mockito.reset;
import static rocks.metaldetector.support.Endpoints.Rest.OAUTH_CALLBACK;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthorizationCodeLoginFilterTest implements WithAssertions {

  @InjectMocks
  private OAuth2AuthorizationCodeLoginFilter underTest;

  @Mock
  private OAuthAuthorizationStateService authorizationStateService;

  @Mock
  private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

  @AfterEach
  void tearDown() {
    reset(authorizationStateService, authenticationDetailsSource);
  }

  @Test
  @DisplayName("Should filter if request URI does equal OAUTH_CALLBACK")
  void test_should_filter() {
    // given
    var request = new MockHttpServletRequest();
    request.setRequestURI(OAUTH_CALLBACK);

    // when
    boolean result = underTest.shouldNotFilter(request);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Should not filter if request URI does not equal OAUTH_CALLBACK")
  void test_should_not_filter() {
    // given
    var request = new MockHttpServletRequest();
    request.setRequestURI("/some/other/endpoint");

    // when
    boolean result = underTest.shouldNotFilter(request);

    // then
    assertThat(result).isTrue();
  }
}
