package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.notification.NotificationService;

import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_FREQUENCY;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_RELEASE_DATE;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
public class NotificationRestController {

  private final NotificationService notificationService;

  @PostMapping(path = NOTIFICATION_ON_FREQUENCY)
  public ResponseEntity<Void> notifyOnFrequency() {
    notificationService.notifyOnFrequency();
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = NOTIFICATION_ON_RELEASE_DATE)
  public ResponseEntity<Void> notifyOnReleaseDate() {
    notificationService.notifyOnReleaseDate();
    return ResponseEntity.ok().build();
  }
}
