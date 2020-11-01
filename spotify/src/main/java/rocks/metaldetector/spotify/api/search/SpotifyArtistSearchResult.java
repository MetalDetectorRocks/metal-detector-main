package rocks.metaldetector.spotify.api.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.SpotifyPaginatedResult;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonPropertyOrder({
    "href",
    "items",
    "limit",
    "next",
    "offset",
    "previous",
    "total"
})
public class SpotifyArtistSearchResult extends SpotifyPaginatedResult {

  @JsonProperty("items")
  private List<SpotifyArtist> items;

  @Builder
  public SpotifyArtistSearchResult(List<SpotifyArtist> items, String href, String next, String previous, int total, int offset, int limit) {
    super(href, limit, next, offset, previous, total);
    this.items = items;
  }
}
