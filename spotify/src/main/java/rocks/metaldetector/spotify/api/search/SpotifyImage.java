package rocks.metaldetector.spotify.api.search;

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
    "url",
    "height",
    "width"
})
public class SpotifyImage {

  @JsonProperty("url")
  private String url;

  @JsonProperty("height")
  private int height;

  @JsonProperty("width")
  private int width;

}
