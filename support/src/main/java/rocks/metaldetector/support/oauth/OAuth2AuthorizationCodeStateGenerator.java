package rocks.metaldetector.support.oauth;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@Component
@Scope(value = SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OAuth2AuthorizationCodeStateGenerator {

  private final StringKeyGenerator stringKeyGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder());
  private String state = null;

  public String generateState() {
    if (state == null || state.isBlank()) {
      this.state = stringKeyGenerator.generateKey();
    }
    return this.state;
  }
}
