package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "releaseId",
    "state"
})
public class ButlerUpdateReleaseStateRequest {

  @JsonProperty("releaseId")
  private long releaseId;

  @JsonProperty("state")
  private String state;

}
