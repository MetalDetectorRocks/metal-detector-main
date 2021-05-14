package rocks.metaldetector.service.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigEntity;
import rocks.metaldetector.persistence.domain.notification.TelegramConfigRepository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.telegram.facade.TelegramMessagingService;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
@Slf4j
public class TelegramConfigServiceImpl implements TelegramConfigService {

  static final String REGISTRATION_SUCCESSFUL_MESSAGE = "You successfully registered for Telegram notifications!";
  static final String REGISTRATION_FAILED_MESSAGE_NOT_READABLE = "The registration id could not be parsed: '%s'";
  static final String REGISTRATION_FAILED_ID_NOT_FOUND = "The registration id could not be found: '%d'";

  private final TelegramConfigRepository telegramConfigRepository;
  private final TelegramConfigTransformer telegramConfigTransformer;
  private final TelegramMessagingService telegramMessagingService;
  private final CurrentUserSupplier currentUserSupplier;

  @Override
  public Optional<TelegramConfigDto> getCurrentUserTelegramConfig() {
    AbstractUserEntity currentUser = currentUserSupplier.get();
    return telegramConfigRepository.findByUser(currentUser).map(telegramConfigTransformer::transform);
  }

  @Override
  @Transactional
  public void updateChatId(String messageText, int chatId) {
    int registrationId;

    try {
      registrationId = Integer.parseInt(messageText.trim());
    }
    catch (NumberFormatException e) {
      telegramMessagingService.sendMessage(chatId, String.format(REGISTRATION_FAILED_MESSAGE_NOT_READABLE, messageText));
      log.warn("Could not parse telegram registration id '{}'", messageText);
      return;
    }

    Optional<TelegramConfigEntity> telegramConfigOptional = telegramConfigRepository.findByRegistrationId(registrationId);
    if (telegramConfigOptional.isPresent()) {
      TelegramConfigEntity telegramConfig = telegramConfigOptional.get();
      telegramConfig.setChatId(chatId);
      telegramConfig.setRegistrationId(null);
      telegramConfigRepository.save(telegramConfig);
      telegramMessagingService.sendMessage(chatId, REGISTRATION_SUCCESSFUL_MESSAGE);
    }
    else {
      telegramMessagingService.sendMessage(chatId, String.format(REGISTRATION_FAILED_ID_NOT_FOUND, registrationId));
      log.warn("Could not set telegram chat id for registration id '{}' - id not found", registrationId);
    }
  }

  @Override
  @Transactional
  public int generateRegistrationId() {
    int registrationId;
    do { registrationId = ThreadLocalRandom.current().nextInt(100_000, 1_000_000); }
    while (telegramConfigRepository.existsByRegistrationId(registrationId));

    AbstractUserEntity currentUser = currentUserSupplier.get();
    TelegramConfigEntity telegramConfig = telegramConfigRepository.findByUser(currentUser)
        .orElseGet(() -> TelegramConfigEntity.builder().build());

    telegramConfig.setRegistrationId(registrationId);
    telegramConfigRepository.save(telegramConfig);
    return registrationId;
  }
}
