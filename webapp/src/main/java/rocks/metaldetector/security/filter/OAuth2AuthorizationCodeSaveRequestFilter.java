package rocks.metaldetector.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateRepository;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.support.oauth.OAuth2AuthorizationCodeStateGenerator;

import java.io.IOException;
import java.util.Arrays;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static rocks.metaldetector.service.user.UserErrorMessages.USER_WITH_ID_NOT_FOUND;

@Component
@AllArgsConstructor
@Slf4j
public class OAuth2AuthorizationCodeSaveRequestFilter extends OncePerRequestFilter {

  private final OAuth2AuthorizationCodeStateGenerator stateGenerator;
  private final JwtsSupport jwtsSupport;
  private final UserRepository userRepository;
  private final OAuthAuthorizationStateRepository authorizationStateRepository;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().equals("/oauth2/authorization/spotify-user");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("authorization")).findFirst().map(Cookie::getValue).orElseThrow(() -> new IllegalStateException("Cookie 'authorization' not found"));
      if (StringUtils.hasText(token) && jwtsSupport.validateJwtToken(token)) {
        Claims claims = jwtsSupport.getClaims(token);
        AbstractUserEntity user = userRepository.findByPublicId(claims.getSubject())
            .orElseThrow(() -> new ResourceNotFoundException(USER_WITH_ID_NOT_FOUND.toDisplayString()));

        OAuthAuthorizationStateEntity authorizationStateEntity = OAuthAuthorizationStateEntity.builder()
            .user(user)
            .state(stateGenerator.generateState())
            .build();
        authorizationStateRepository.save(authorizationStateEntity);
      }
    }
    catch (Exception e) {
      log.error("Cannot authenticate user starting authorization code flow", e);
      response.sendError(SC_FORBIDDEN);
    }

    filterChain.doFilter(request, response);
  }
}
