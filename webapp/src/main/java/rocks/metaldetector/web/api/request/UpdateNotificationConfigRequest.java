package rocks.metaldetector.web.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateNotificationConfigRequest {

  @Min(0L)
  private int frequencyInWeeks;
  private boolean notificationAtReleaseDate;
  private boolean notificationAtAnnouncementDate;
  private boolean notify;

}
