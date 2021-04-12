package rocks.metaldetector.service.telegram;

import rocks.metaldetector.web.api.request.TelegramUpdate;

public interface TelegramUpdateService {

  void processUpdate(TelegramUpdate update);
}
