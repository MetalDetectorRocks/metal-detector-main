package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
    "totalCountRequested",
    "totalCountImported"
})
@NoArgsConstructor
public class ButlerImportJobResponse {

  @JsonProperty("totalCountRequested")
  private int totalCountRequested;

  @JsonProperty("totalCountImported")
  private int totalCountImported;

}
