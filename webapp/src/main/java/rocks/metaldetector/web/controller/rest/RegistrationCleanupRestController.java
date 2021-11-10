package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.cleanup.RegistrationCleanupService;
import rocks.metaldetector.support.Endpoints;

@RestController
@AllArgsConstructor
public class RegistrationCleanupRestController {

  private final RegistrationCleanupService registrationCleanupService;

  @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
  @PostMapping(Endpoints.Rest.CLEANUP)
  public ResponseEntity<Void> cleanup() {
    registrationCleanupService.cleanupUsersWithExpiredToken();
    return ResponseEntity.ok().build();
  }
}
