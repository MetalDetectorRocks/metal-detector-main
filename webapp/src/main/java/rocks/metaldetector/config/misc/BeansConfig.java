package rocks.metaldetector.config.misc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Configuration
public class BeansConfig {

  @Bean
  public AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource() {
    return new WebAuthenticationDetailsSource();
  }
}
