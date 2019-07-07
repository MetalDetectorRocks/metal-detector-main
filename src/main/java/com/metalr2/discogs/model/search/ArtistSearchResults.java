package com.metalr2.discogs.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({
        "pagination",
        "results"
})
public class ArtistSearchResults {

  @JsonProperty("pagination")
  private Pagination pagination;

  @JsonProperty("results")
  private List<ArtistSearchResult> results;

}
