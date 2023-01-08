package rocks.metaldetector.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rocks.metaldetector.web.api.auth.LoginRequest;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final ObjectMapper objectMapper;

  public CustomUsernamePasswordAuthenticationFilter(
      AuthenticationConfiguration authenticationConfiguration,
      ObjectMapper objectMapper
  ) throws Exception {
    super(authenticationConfiguration.getAuthenticationManager());
    this.objectMapper = objectMapper;
  }

  @Override
  @SneakyThrows
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
    authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
    Authentication authentication = getAuthenticationManager().authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return authentication;
  }
}
