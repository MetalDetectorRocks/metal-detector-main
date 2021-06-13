package rocks.metaldetector.support.oauth;

import java.util.function.Supplier;

public interface OAuth2AccessTokenSupplier extends Supplier<String> {

  void setRegistrationId(String registrationId);
}
