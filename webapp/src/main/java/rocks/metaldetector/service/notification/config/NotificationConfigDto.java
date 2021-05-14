package rocks.metaldetector.service.notification.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationConfigDto {

  private boolean notify;
  private int frequencyInWeeks;
  private boolean notificationAtReleaseDate;
  private boolean notificationAtAnnouncementDate;
  private String channel;
}
