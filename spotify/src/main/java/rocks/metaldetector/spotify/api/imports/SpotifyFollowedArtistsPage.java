package rocks.metaldetector.spotify.api.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.spotify.api.SpotifyArtist;

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
    "total"
})
public class SpotifyFollowedArtistsPage {

  @JsonProperty("href")
  protected String href;

  @JsonProperty("items")
  private List<SpotifyArtist> items;

  @JsonProperty("limit")
  private int limit;

  @JsonProperty("next")
  private String next;

  @JsonProperty("total")
  private int total;
}
