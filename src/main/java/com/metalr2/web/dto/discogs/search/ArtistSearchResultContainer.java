package com.metalr2.web.dto.discogs.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({
        "pagination",
        "results"
})
public class ArtistSearchResultContainer {

  @JsonProperty("pagination")
  private Pagination pagination;

  @JsonProperty("results")
  private List<ArtistSearchResult> results;

}
