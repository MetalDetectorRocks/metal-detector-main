package rocks.metaldetector.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.SecurityProperties;
import rocks.metaldetector.web.api.auth.LoginResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final AuthenticationFacade authenticationFacade;
  private final JwtsSupport jwtsSupport;
  private final SecurityProperties securityProperties;
  private final RefreshTokenService refreshTokenService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    AbstractUserEntity user = authenticationFacade.getCurrentUser();
    String token = jwtsSupport.generateToken(
        user.getPublicId(),
        Duration.ofMinutes(securityProperties.getAccessTokenExpirationInMin())
    );
    LoginResponse loginResponse = LoginResponse.builder()
        .username(user.getUsername())
        .accessToken(token)
        .roles(user.getUserRoles().stream().map(UserRole::getDisplayName).collect(Collectors.toList()))
        .build();

    ResponseCookie cookie = refreshTokenService.createRefreshTokenCookie(user.getUsername());
    response.setHeader(SET_COOKIE, cookie.toString());
    response.setContentType(APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getOutputStream(), loginResponse);
  }
}
