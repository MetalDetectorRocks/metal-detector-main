package rocks.metaldetector.web.controller.rest.auth;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.auth.RefreshTokenData;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.web.api.auth.LoginResponse;
import rocks.metaldetector.web.api.auth.AuthenticationResponse;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;

@RestController
@AllArgsConstructor
public class AuthenticationRestController {

  private final RefreshTokenService refreshTokenService;

  @GetMapping(path = AUTHENTICATION, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthenticationResponse> authenticated(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {
    return ResponseEntity.ok(
        AuthenticationResponse.builder()
            .authenticated(refreshTokenService.isValid(refreshToken))
            .build()
    );
  }

  @GetMapping(value = REFRESH_ACCESS_TOKEN, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> refreshAccessToken(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
    RefreshTokenData refreshTokenData = refreshTokenService.refreshTokens(refreshToken);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(SET_COOKIE, refreshTokenData.refreshToken().toString());

    return ResponseEntity.ok()
        .headers(httpHeaders)
        .body(refreshTokenData.asLoginResponse());
  }
}
