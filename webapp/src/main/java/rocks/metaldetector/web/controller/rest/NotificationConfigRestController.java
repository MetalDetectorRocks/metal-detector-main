package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.notification.NotificationConfigDto;
import rocks.metaldetector.service.notification.NotificationService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.request.UpdateNotificationConfigRequest;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(Endpoints.Rest.NOTIFICATION_CONFIG)
@AllArgsConstructor
public class NotificationConfigRestController {

  private final NotificationService notificationService;
  private final ModelMapper modelMapper;

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  ResponseEntity<NotificationConfigDto> getCurrentUsersNotificationConfig() {
    NotificationConfigDto notificationConfigDto = notificationService.getCurrentUserNotificationConfig();
    return ResponseEntity.ok(notificationConfigDto);
  }

  @PutMapping(produces = APPLICATION_JSON_VALUE,
              consumes = APPLICATION_JSON_VALUE)
  ResponseEntity<Void> updateCurrentUserNotificationConfig(@Valid @RequestBody UpdateNotificationConfigRequest updateNotificationConfigRequest) {
    NotificationConfigDto notificationConfigDto = modelMapper.map(updateNotificationConfigRequest, NotificationConfigDto.class);
    notificationService.updateCurrentUserNotificationConfig(notificationConfigDto);
    return ResponseEntity.ok().build();
  }
}
