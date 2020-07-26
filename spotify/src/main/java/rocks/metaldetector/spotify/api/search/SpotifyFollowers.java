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
    "href",
    "total"
})
public class SpotifyFollowers {

  @JsonProperty("href")
  private String href;

  @JsonProperty("total")
  private int total;

}
