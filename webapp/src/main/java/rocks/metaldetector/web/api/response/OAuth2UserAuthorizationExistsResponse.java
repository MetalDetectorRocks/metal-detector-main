package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2UserAuthorizationExistsResponse {

  private boolean exists;
}
