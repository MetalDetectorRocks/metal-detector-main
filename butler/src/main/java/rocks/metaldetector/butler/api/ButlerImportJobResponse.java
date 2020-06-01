package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
    "totalCountRequested",
    "totalCountImported",
    "startTime",
    "endTime"
})
@NoArgsConstructor
public class ButlerImportJobResponse {

  @JsonProperty("totalCountRequested")
  private int totalCountRequested;

  @JsonProperty("totalCountImported")
  private int totalCountImported;

  @JsonProperty("startTime")
  private LocalDateTime startTime;

  @JsonProperty("endTime")
  private LocalDateTime endTime;

}
