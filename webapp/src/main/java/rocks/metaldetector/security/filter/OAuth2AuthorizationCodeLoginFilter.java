package rocks.metaldetector.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateEntity;
import rocks.metaldetector.persistence.domain.user.OAuthAuthorizationStateRepository;

import java.io.IOException;

@Component
@AllArgsConstructor
public class OAuth2AuthorizationCodeLoginFilter extends OncePerRequestFilter {

  private final OAuthAuthorizationStateRepository authorizationStateRepository;
  private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().equals("/rest/v1/oauth/callback");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String state = request.getParameter("state");
    OAuthAuthorizationStateEntity authorizationState = authorizationStateRepository.findByState(state);
    if (authorizationState != null) {
      AbstractUserEntity user = authorizationState.getUser();
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      authentication.setDetails(authenticationDetailsSource.buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
//      authorizationStateRepository.deleteByState(state); // ToDo not working, something with transactions
    }

    filterChain.doFilter(request, response);
  }
}
