package rocks.metaldetector.web.controller.rest.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.auth.RefreshTokenData;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.web.api.auth.AuthenticationResponse;
import rocks.metaldetector.web.api.auth.LoginResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;

@Slf4j
@RestController
@AllArgsConstructor
@ConditionalOnProperty(
    name = "security.mock-mode",
    havingValue = "true"
)
public class MockRefreshTokenRestController {

  private final UserRepository userRepository;
  private final RefreshTokenService refreshTokenService;

  @GetMapping(path = AUTHENTICATION, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthenticationResponse> authenticated() {
    return ResponseEntity.ok(new AuthenticationResponse(true));
  }

  @GetMapping(value = REFRESH_ACCESS_TOKEN, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> refreshAccessToken() {
    UserEntity administrator = userRepository.getByUsername("Administrator");
    String accessToken = refreshTokenService.createAccessToken(administrator.getPublicId());
    RefreshTokenData refreshTokenData = new RefreshTokenData(
        administrator.getUsername(),
        administrator.getUserRoleNames(),
        accessToken,
        null
    );
    return ResponseEntity.ok().body(refreshTokenData.asLoginResponse());
  }
}
