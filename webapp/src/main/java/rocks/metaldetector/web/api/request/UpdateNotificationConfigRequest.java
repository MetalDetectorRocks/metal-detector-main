package rocks.metaldetector.web.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateNotificationConfigRequest {

  private boolean notificationAtReleaseDate;
  private boolean notificationAtAnnouncementDate;
  private boolean notifyReissues;

  @Min(0)
  private int frequencyInWeeks;

  @NotBlank
  private String channel;
}
