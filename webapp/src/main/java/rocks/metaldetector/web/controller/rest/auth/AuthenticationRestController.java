package rocks.metaldetector.web.controller.rest.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import rocks.metaldetector.service.auth.ResetPasswordService;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.web.api.auth.InitResetPasswordRequest;
import rocks.metaldetector.web.api.auth.RegisterUserRequest;
import rocks.metaldetector.web.api.auth.RegistrationVerificationRequest;
import rocks.metaldetector.web.api.auth.RegistrationVerificationResponse;
import rocks.metaldetector.web.api.auth.ResetPasswordRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTER;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTRATION_VERIFICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REQUEST_PASSWORD_RESET;
import static rocks.metaldetector.support.Endpoints.Rest.RESET_PASSWORD;

@Slf4j
@RestController
@AllArgsConstructor
public class AuthenticationRestController {

  private final UserService userService;
  private final ResetPasswordService resetPasswordService;

  @PostMapping(value = REGISTER, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
    userService.createUser(registerUserRequest);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = REGISTRATION_VERIFICATION, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<RegistrationVerificationResponse> verifyUser(@Valid @RequestBody RegistrationVerificationRequest verificationRequest) {
    RegistrationVerificationResponse response = userService.verifyEmailToken(verificationRequest.getToken());
    return ResponseEntity.ok(response);
  }

  @PostMapping(value = REQUEST_PASSWORD_RESET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody InitResetPasswordRequest initResetPasswordRequest) {
    resetPasswordService.requestPasswordReset(initResetPasswordRequest);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = RESET_PASSWORD, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
    resetPasswordService.resetPassword(resetPasswordRequest);
    return ResponseEntity.ok().build();
  }
}
