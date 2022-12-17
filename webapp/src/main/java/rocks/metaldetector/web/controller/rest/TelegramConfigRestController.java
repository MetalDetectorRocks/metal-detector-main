package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.notification.config.TelegramConfigService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.TELEGRAM_CONFIG;

@RestController
@AllArgsConstructor
public class TelegramConfigRestController {

  private final TelegramConfigService telegramConfigService;

  @PostMapping(path = TELEGRAM_CONFIG, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Integer> generateRegistrationId() {
    int registrationId = telegramConfigService.generateRegistrationId();
    return ResponseEntity.ok(registrationId);
  }

  @DeleteMapping(path = TELEGRAM_CONFIG)
  public ResponseEntity<Void> deleteTelegramConfig() {
    telegramConfigService.deleteCurrentUserTelegramConfig();
    return ResponseEntity.ok().build();
  }
}
