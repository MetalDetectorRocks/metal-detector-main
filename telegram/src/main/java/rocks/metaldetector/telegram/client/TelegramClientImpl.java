package rocks.metaldetector.telegram.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.support.exceptions.ExternalServiceException;
import rocks.metaldetector.telegram.api.TelegramMessage;
import rocks.metaldetector.telegram.api.TelegramSendMessageRequest;
import rocks.metaldetector.telegram.config.TelegramProperties;

@Component
@Profile({"default", "preview", "prod"})
public class TelegramClientImpl implements TelegramClient {

  static final String METHOD_ENDPOINT_NAME = "/bot{botId}/sendMessage";

  private final RestOperations telegramRestOperations;
  private final TelegramProperties telegramProperties;

  public TelegramClientImpl(@Qualifier("telegramRestOperations") RestOperations telegramRestOperations,
                            TelegramProperties telegramProperties) {
    this.telegramRestOperations = telegramRestOperations;
    this.telegramProperties = telegramProperties;
  }

  @Override
  public TelegramMessage sendMessage(TelegramSendMessageRequest request) {
    ResponseEntity<TelegramMessage> responseEntity = telegramRestOperations.postForEntity(
        telegramProperties.getRestBaseUrl() + METHOD_ENDPOINT_NAME,
        request,
        TelegramMessage.class,
        telegramProperties.getBotId()
    );

    TelegramMessage message = responseEntity.getBody();
    var shouldNotHappen = message == null || !responseEntity.getStatusCode().is2xxSuccessful();
    if (shouldNotHappen) {
      throw new ExternalServiceException("Could not send message to chat '" + request.getChatId() + "' (Response code: " + responseEntity.getStatusCode() + ")");
    }

    return message;
  }
}
