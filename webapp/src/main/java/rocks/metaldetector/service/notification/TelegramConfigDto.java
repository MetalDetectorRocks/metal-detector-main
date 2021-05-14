package rocks.metaldetector.service.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramConfigDto {

  private Integer registrationId;
  private Integer chatId;
}
