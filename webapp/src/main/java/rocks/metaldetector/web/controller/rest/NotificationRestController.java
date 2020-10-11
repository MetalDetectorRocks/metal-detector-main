package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.notification.NotificationService;
import rocks.metaldetector.support.Endpoints;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
public class NotificationRestController {

  private final NotificationService notificationService;

  @PostMapping(path = Endpoints.Rest.NOTIFY + "/{publicUserId}")
  public ResponseEntity<Void> notifyUser(@PathVariable String publicUserId) {
    notificationService.notifyUser(publicUserId);
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = Endpoints.Rest.NOTIFY)
  public ResponseEntity<Void> notifyAllUsers() {
    notificationService.notifyAllUsers();
    return ResponseEntity.ok().build();
  }
}
