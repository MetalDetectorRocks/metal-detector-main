package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.notification.NotificationService;

@RestController
@AllArgsConstructor
public class NotificationRestController {

  private final NotificationService notificationService;

  @GetMapping(path = Endpoints.Rest.NOTIFY + "/{publicUserId}")
  public ResponseEntity<Void> notifyUser(@PathVariable String publicUserId) {
    notificationService.notifyUser(publicUserId);
    return ResponseEntity.ok().build();
  }

  @GetMapping(path = Endpoints.Rest.NOTIFY)
  public ResponseEntity<Void> notifyAllUsers() {
    notificationService.notifyAllUsers();
    return ResponseEntity.ok().build();
  }
}
