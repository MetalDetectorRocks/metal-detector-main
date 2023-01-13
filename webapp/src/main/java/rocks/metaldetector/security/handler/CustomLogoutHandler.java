package rocks.metaldetector.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import rocks.metaldetector.service.auth.RefreshTokenService;

import java.util.stream.Stream;

import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

  private final RefreshTokenService refreshTokenService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    Stream.of(request.getCookies())
        .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
        .map(Cookie::getValue)
        .forEach(refreshTokenService::removeRefreshToken);
  }
}
