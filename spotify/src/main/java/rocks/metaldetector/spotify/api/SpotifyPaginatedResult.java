package rocks.metaldetector.spotify.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotifyPaginatedResult {

  @JsonProperty("href")
  protected String href;

  @JsonProperty("limit")
  protected int limit;

  @JsonProperty("next")
  protected String next;

  @JsonProperty("offset")
  protected int offset;

  @JsonProperty("previous")
  protected String previous;

  @JsonProperty("total")
  protected int total;
}
