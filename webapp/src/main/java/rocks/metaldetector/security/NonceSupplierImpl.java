package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

@Component
@AllArgsConstructor
public class NonceSupplierImpl implements NonceSupplier {

  private final Random secureRandom;
  private final MessageDigest messageDigest;

  @Override
  public String get() {
    byte[] randomBytes = new byte[32];
    secureRandom.nextBytes(randomBytes);
    return new String(Base64.getEncoder().encode(messageDigest.digest(randomBytes)), Charset.defaultCharset());
  }
}
