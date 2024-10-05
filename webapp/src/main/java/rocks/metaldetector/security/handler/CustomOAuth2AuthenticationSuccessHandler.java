package rocks.metaldetector.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.auth.RefreshTokenService;

import static jakarta.servlet.http.HttpServletResponse.SC_FOUND;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final AuthenticationFacade authenticationFacade;
  private final RefreshTokenService refreshTokenService;
  private String frontendOrigin;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    AbstractUserEntity user = authenticationFacade.getCurrentUser();
    ResponseCookie cookie = refreshTokenService.createRefreshTokenCookie(user);
    response.setHeader(SET_COOKIE, cookie.toString());
    response.setStatus(SC_FOUND);
    response.addHeader(LOCATION, frontendOrigin);
  }

  @Value("${frontend.origin}")
  void setFrontendOrigin(String frontendOrigin) {
    this.frontendOrigin = frontendOrigin;
  }
}
