package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.web.api.response.AuthenticationResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;

@RestController
@AllArgsConstructor
public class AuthenticationRestController {

  private final AuthenticationFacade authenticationFacade;

  @GetMapping(path = AUTHENTICATION,
              produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthenticationResponse> authenticated() {
    return ResponseEntity.ok(
        AuthenticationResponse.builder()
            .authenticated(authenticationFacade.isAuthenticated())
            .build()
    );
  }
}
