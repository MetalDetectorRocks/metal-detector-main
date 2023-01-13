package rocks.metaldetector.web.controller.rest.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import rocks.metaldetector.service.auth.RefreshTokenData;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.web.api.auth.LoginResponse;
import rocks.metaldetector.web.api.auth.AuthenticationResponse;
import rocks.metaldetector.web.api.auth.RegisterUserRequest;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTER;

@RestController
@AllArgsConstructor
@Slf4j
public class AuthenticationRestController {

  private final RefreshTokenService refreshTokenService;
  private final UserService userService;

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

  @PostMapping(value = REGISTER, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
    userService.createUser(registerUserRequest);
    return ResponseEntity.ok().build();
  }
}
