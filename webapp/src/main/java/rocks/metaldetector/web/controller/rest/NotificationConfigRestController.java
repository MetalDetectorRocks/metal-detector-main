package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.notification.config.NotificationConfigDto;
import rocks.metaldetector.service.notification.config.NotificationConfigService;
import rocks.metaldetector.service.notification.config.TelegramConfigDto;
import rocks.metaldetector.service.notification.config.TelegramConfigService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.web.api.request.UpdateNotificationConfigRequest;
import rocks.metaldetector.web.api.response.NotificationConfigResponse;
import rocks.metaldetector.web.transformer.NotificationConfigResponseTransformer;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(Endpoints.Rest.NOTIFICATION_CONFIG)
@AllArgsConstructor
public class NotificationConfigRestController {

  private final NotificationConfigService notificationConfigService;
  private final TelegramConfigService telegramConfigService;
  private final NotificationConfigResponseTransformer notificationConfigResponseTransformer;

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  ResponseEntity<NotificationConfigResponse> getCurrentUsersNotificationConfigs() {
    List<NotificationConfigDto> notificationConfigDtos = notificationConfigService.getCurrentUserNotificationConfigs();
    Optional<TelegramConfigDto> telegramConfigOptional = telegramConfigService.getCurrentUserTelegramConfig();
    return ResponseEntity.ok(notificationConfigResponseTransformer.transformResponse(notificationConfigDtos, telegramConfigOptional.orElse(null)));
  }

  @PutMapping(consumes = APPLICATION_JSON_VALUE)
  ResponseEntity<Void> updateCurrentUserNotificationConfig(@Valid @RequestBody UpdateNotificationConfigRequest updateNotificationConfigRequest) {
    NotificationConfigDto notificationConfigDto = notificationConfigResponseTransformer.transformUpdateRequest(updateNotificationConfigRequest);
    notificationConfigService.updateCurrentUserNotificationConfig(notificationConfigDto);
    return ResponseEntity.ok().build();
  }
}
