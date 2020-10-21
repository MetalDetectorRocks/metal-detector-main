package rocks.metaldetector.spotify.api.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
    "text",
    "type"
})
public class SpotifyCopyright {

  @JsonProperty("text")
  private String text;

  @JsonProperty("type")
  private String type;
}
