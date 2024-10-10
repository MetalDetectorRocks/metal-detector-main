package rocks.metaldetector.security.handler;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.auth.RefreshTokenService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.FOUND;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2AuthenticationSuccessHandlerTest implements WithAssertions {

  private static final ResponseCookie RESPONSE_COOKIE = ResponseCookie.from("under", "test").build();

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private RefreshTokenService refreshTokenService;

  @InjectMocks
  private CustomOAuth2AuthenticationSuccessHandler underTest;

  private final MockHttpServletRequest request = new MockHttpServletRequest();
  private final MockHttpServletResponse response = new MockHttpServletResponse();

  @AfterEach
  void tearDown() {
    reset(authenticationFacade, refreshTokenService);
  }

  @Test
  @DisplayName("authenticationFacade is called")
  void test_authentication_facade_is_called() {
    // given
    doReturn(RESPONSE_COOKIE).when(refreshTokenService).createRefreshTokenCookie(any());

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    verify(authenticationFacade).getCurrentUser();
  }

  @Test
  @DisplayName("refreshTokenService is called")
  void test_refresh_token_service_is_called() {
    // given
    var mockUser = mock(AbstractUserEntity.class);
    doReturn(mockUser).when(authenticationFacade).getCurrentUser();
    doReturn(RESPONSE_COOKIE).when(refreshTokenService).createRefreshTokenCookie(mockUser);

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    verify(refreshTokenService).createRefreshTokenCookie(mockUser);
  }

  @Test
  @DisplayName("cookie header is set")
  void test_cookie_header_is_set() {
    // given
    doReturn(RESPONSE_COOKIE).when(refreshTokenService).createRefreshTokenCookie(any());

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    assertThat(response.getHeader(SET_COOKIE)).isEqualTo("under=test");
  }

  @Test
  @DisplayName("location header is set")
  void test_location_header_is_set() {
    // given
    underTest.setFrontendOrigin("someOrigin");
    doReturn(RESPONSE_COOKIE).when(refreshTokenService).createRefreshTokenCookie(any());

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    assertThat(response.getHeader(LOCATION)).isEqualTo("someOrigin");
  }

  @Test
  @DisplayName("http status is set")
  void test_http_status_is_set() {
    // given
    doReturn(RESPONSE_COOKIE).when(refreshTokenService).createRefreshTokenCookie(any());

    // when
    underTest.onAuthenticationSuccess(request, response, null);

    // then
    assertThat(response.getStatus()).isEqualTo(FOUND.value());
  }
}