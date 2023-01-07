package rocks.metaldetector.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.auth.RefreshTokenService;

import static org.mockito.Mockito.*;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;

@ExtendWith(MockitoExtension.class)
class CustomLogoutHandlerTest implements WithAssertions {

  @Mock
  private RefreshTokenService refreshTokenService;

  @InjectMocks
  private CustomLogoutHandler underTest;

  @Test
  @DisplayName("should remove refresh token entity via service")
  void should_remove_refresh_token_entity_via_service() {
    // given
    String tokenCookieValue = "eyRefreshToken";
    var request = mock(HttpServletRequest.class);
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, tokenCookieValue);
    doReturn(new Cookie[]{cookie}).when(request).getCookies();

    // when
    underTest.logout(request, null, null);

    // then
    verify(refreshTokenService).removeRefreshToken(tokenCookieValue);
  }
}