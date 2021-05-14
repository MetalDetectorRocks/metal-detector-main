package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.web.api.EmailConfig;
import rocks.metaldetector.web.api.TelegramConfig;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationConfigResponse {

  private EmailConfig emailConfig;
  private TelegramConfig telegramConfig;
}
