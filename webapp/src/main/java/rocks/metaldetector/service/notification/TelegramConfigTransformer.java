package rocks.metaldetector.service.notification;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigEntity;

@Component
public class TelegramConfigTransformer {

  public TelegramConfigDto transform(TelegramConfigEntity telegramConfig) {
    return TelegramConfigDto.builder()
        .registrationId(telegramConfig.getRegistrationId())
        .chatId(telegramConfig.getChatId())
        .build();
  }
}
