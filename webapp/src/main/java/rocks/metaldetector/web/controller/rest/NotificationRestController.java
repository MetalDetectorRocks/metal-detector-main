package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.notification.messaging.NotificationScheduler;

import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_ANNOUNCEMENT_DATE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_FREQUENCY;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_RELEASE_DATE;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
@Profile("!prod")
public class NotificationRestController {

  private final NotificationScheduler notificationScheduler;

  @PostMapping(path = NOTIFICATION_ON_FREQUENCY)
  public ResponseEntity<Void> notifyOnFrequency() {
    notificationScheduler.notifyOnFrequency();
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = NOTIFICATION_ON_RELEASE_DATE)
  public ResponseEntity<Void> notifyOnReleaseDate() {
    notificationScheduler.notifyOnReleaseDate();
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = NOTIFICATION_ON_ANNOUNCEMENT_DATE)
  public ResponseEntity<Void> notifyOnAnnouncementDate() {
    notificationScheduler.notifyOnAnnouncementDate();
    return ResponseEntity.ok().build();
  }
}
