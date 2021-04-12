package rocks.metaldetector.service.telegram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.notification.NotificationService;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class TelegramUpdateServiceImpl implements TelegramUpdateService {

  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @Override
  public void processUpdate(TelegramUpdate update) {
    registerForTelegramNotifications(update);
  }

  private void registerForTelegramNotifications(TelegramUpdate update) {
    String messageText = update.getMessage().getText();
    Optional<AbstractUserEntity> user = userRepository.findByEmail(messageText);
    if (user.isPresent()) {
      notificationService.updateTelegramChatId(user.get().getId(), update.getMessage().getChat().getId());
    } else {
      log.warn("Could not update telegram chat id for user with email '{}' - user not found", messageText);
    }
  }
}
