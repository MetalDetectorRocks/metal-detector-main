package rocks.metaldetector.service.telegram;

import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.web.api.request.TelegramUpdate;

public interface TelegramService {

  void processUpdate(TelegramUpdate update);

  void sendMessage(AbstractUserEntity user, String message);
}
