package rocks.metaldetector.service.telegram;

import rocks.metaldetector.web.api.request.TelegramUpdate;

public interface TelegramService {

  void processUpdate(TelegramUpdate update);
}
