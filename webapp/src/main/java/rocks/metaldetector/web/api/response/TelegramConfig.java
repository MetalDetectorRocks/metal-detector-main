package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramConfig {

  private boolean notify;
  private int frequencyInWeeks;
  private boolean notificationAtReleaseDate;
  private boolean notificationAtAnnouncementDate;
  private Integer registrationId;
  private boolean notificationsActivated;
}
