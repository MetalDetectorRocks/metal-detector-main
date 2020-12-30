package rocks.metaldetector.spotify.api.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
public class SpotifySavedAlbumsPage extends SpotifyPaginatedResult {

  @JsonProperty("items")
  private List<SpotifySavedAlbumsPageItem> items;

  @Builder
  public SpotifySavedAlbumsPage(List<SpotifySavedAlbumsPageItem> items, String href, String next, String previous, int total, int offset, int limit) {
    super(href, limit, next, offset, previous, total);
    this.items = items;
  }
}
