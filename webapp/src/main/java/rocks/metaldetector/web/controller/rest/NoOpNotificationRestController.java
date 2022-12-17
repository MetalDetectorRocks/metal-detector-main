package rocks.metaldetector.web.controller.rest;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_ANNOUNCEMENT_DATE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_FREQUENCY;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_RELEASE_DATE;

@RestController
@Profile("prod")
public class NoOpNotificationRestController {

  @PostMapping(path = NOTIFICATION_ON_FREQUENCY)
  public ResponseEntity<Void> notifyOnFrequency() {
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = NOTIFICATION_ON_RELEASE_DATE)
  public ResponseEntity<Void> notifyOnReleaseDate() {
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = NOTIFICATION_ON_ANNOUNCEMENT_DATE)
  public ResponseEntity<Void> notifyOnAnnouncementDate() {
    return ResponseEntity.ok().build();
  }
}
