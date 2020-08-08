package rocks.metaldetector.spotify.api.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
    "href",
    "items",
    "limit",
    "next",
    "offset",
    "previous",
    "total"
})
public class SpotifyArtistSearchResult {

  @JsonProperty("href")
  private String href;

  @JsonProperty("items")
  private List<SpotifyArtist> artists;

  @JsonProperty("limit")
  private int limit;

  @JsonProperty("next")
  private String next;

  @JsonProperty("offset")
  private int offset;

  @JsonProperty("previous")
  private String previous;

  @JsonProperty("total")
  private int total;

}
