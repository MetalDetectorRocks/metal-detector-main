package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.LoginService;
import rocks.metaldetector.web.api.request.LoginRequest;
import rocks.metaldetector.web.api.response.LoginResponse;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.LOGIN;

@RestController
@AllArgsConstructor
public class LoginRestController {

  private final LoginService loginService;

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping(value = LOGIN, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest request) {
    LoginResponse response = loginService.loginUser(request);
    return ResponseEntity.ok(response);
  }
}
