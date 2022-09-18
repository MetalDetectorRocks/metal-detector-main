package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.user.AuthService;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.web.api.request.LoginRequest;
import rocks.metaldetector.web.api.response.AuthenticationResponse;
import rocks.metaldetector.web.api.response.LoginResponse;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.LOGIN;

@RestController
@AllArgsConstructor
public class AuthenticationRestController {

  private final AuthenticationFacade authenticationFacade;
  private final AuthService authService;
  private final JwtsSupport jwtsSupport;

  // TODO: should be removed in near future
  @GetMapping(path = AUTHENTICATION, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthenticationResponse> authenticated() {
    return ResponseEntity.ok(
        AuthenticationResponse.builder()
            .authenticated(authenticationFacade.isAuthenticated())
            .build()
    );
  }

  @PostMapping(value = LOGIN, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest request) {
    LoginResponse response = authService.loginUser(request);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(SET_COOKIE, jwtsSupport.createAccessTokenCookie(response.getToken()).toString());
    return ResponseEntity.ok().headers(httpHeaders).body(response);
  }
}
