package rocks.metaldetector.web.controller.rest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.telegram.TelegramUpdateService;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_TELEGRAM;

@RestController
@Slf4j
public class TelegramRestController {

  private final TelegramUpdateService telegramUpdateService;
  private final String botId;

  public TelegramRestController(TelegramUpdateService telegramUpdateService, @Value("${telegram.bot-id}") String botId) {
    this.telegramUpdateService = telegramUpdateService;
    this.botId = botId;
  }

  @PostMapping(path = NOTIFICATION_TELEGRAM + "/{botId}", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> handleWebhook(@PathVariable String botId, @RequestBody @Valid TelegramUpdate update) {
    if (this.botId.equals(botId)) {
      telegramUpdateService.processUpdate(update);
      return ResponseEntity.ok().build();
    }
    log.warn("False botId called");
    return ResponseEntity.status(FORBIDDEN).build();
  }
}
