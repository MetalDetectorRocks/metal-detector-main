package rocks.metaldetector.support.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Component;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@Component
@Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
@RequiredArgsConstructor
public class OAuth2AuthorizationCodeStateGenerator {

  private final StringKeyGenerator stringKeyGenerator;
  private String state = null;

  public String generateState() {
    if (this.state == null) {
      this.state = stringKeyGenerator.generateKey();
    }
    return this.state;
  }

  // For testing purposes only
  void setState(String state) {
    if (this.state == null) {
      this.state = state;
    }
  }
}
