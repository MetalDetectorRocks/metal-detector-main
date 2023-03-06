package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.auth.RegistrationCleanupService;

import static rocks.metaldetector.support.Endpoints.Rest.REGISTRATION_CLEANUP;

@RestController
@AllArgsConstructor
public class RegistrationCleanupRestController {

  private final RegistrationCleanupService registrationCleanupService;

  @PostMapping(path = REGISTRATION_CLEANUP)
  public ResponseEntity<Void> cleanup() {
    registrationCleanupService.cleanupUsersWithExpiredToken();
    return ResponseEntity.ok().build();
  }
}
