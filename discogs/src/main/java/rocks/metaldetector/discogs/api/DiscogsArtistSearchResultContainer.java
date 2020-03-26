package rocks.metaldetector.discogs.api;

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
        "pagination",
        "results"
})
public class DiscogsArtistSearchResultContainer {

  @JsonProperty("pagination")
  private DiscogsPagination pagination;

  @JsonProperty("results")
  private List<DiscogsArtistSearchResult> results;

  @Override
  public String toString() {
    return String.format("%s search result(s)", results.size());
  }
}
