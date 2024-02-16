package rocks.metaldetector.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateRepository;
import rocks.metaldetector.persistence.domain.user.RefreshTokenEntity;
import rocks.metaldetector.persistence.domain.user.RefreshTokenRepository;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.service.exceptions.UnauthorizedException;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.support.oauth.OAuth2AuthorizationCodeStateGenerator;

import java.io.IOException;
import java.util.Arrays;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;
import static rocks.metaldetector.support.oauth.OAuth2ClientConfig.OAUTH_AUTHORIZATION_ENDPOINT;

@Component
@AllArgsConstructor
@Slf4j
public class OAuth2AuthorizationCodeSaveRequestFilter extends OncePerRequestFilter {

  private final OAuth2AuthorizationCodeStateGenerator stateGenerator;
  private final OAuthAuthorizationStateRepository authorizationStateRepository;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith(OAUTH_AUTHORIZATION_ENDPOINT);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = Arrays.stream(request.getCookies())
          .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .map(Cookie::getValue)
          .orElseThrow(() -> new IllegalStateException("Cookie '" + REFRESH_TOKEN_COOKIE_NAME + "' not found"));
      if (refreshTokenService.isValid(token)) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.getByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
        AbstractUserEntity user = refreshTokenEntity.getUser();

        OAuthAuthorizationStateEntity authorizationStateEntity = OAuthAuthorizationStateEntity.builder()
            .user(user)
            .state(stateGenerator.generateState())
            .build();
        authorizationStateRepository.save(authorizationStateEntity);
      }
      else {
        throw new UnauthorizedException();
      }
    }
    catch (Exception e) {
      log.error("Cannot authenticate user starting authorization code flow", e);
      response.sendError(SC_FORBIDDEN);
    }

    filterChain.doFilter(request, response);
  }
}
