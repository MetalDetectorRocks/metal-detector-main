package rocks.metaldetector.web.dto.discogs.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({
        "pagination",
        "results"
})
public class DiscogsArtistSearchResultContainer {

  @JsonProperty("pagination")
  private DiscogsPagination discogsPagination;

  @JsonProperty("results")
  private List<DiscogsArtistSearchResult> results;

  @Override
  public String toString() {
    return String.format("%s search result(s)", results.size());
  }
}
