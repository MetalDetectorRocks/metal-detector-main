package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.notification.TelegramConfigService;
import rocks.metaldetector.support.Endpoints;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(Endpoints.Rest.TELEGRAM_CONFIG)
@AllArgsConstructor
public class TelegramConfigRestController {

  private final TelegramConfigService telegramConfigService;

  @PostMapping(produces = APPLICATION_JSON_VALUE)
  ResponseEntity<Integer> generateRegistrationId() {
    int registrationId = telegramConfigService.generateRegistrationId();
    return ResponseEntity.ok(registrationId);
  }
}
