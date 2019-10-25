package com.metalr2.web.dto.discogs.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "last",
        "next"
})
public class PaginationUrls {

  @JsonProperty("last")
  private String last;

  @JsonProperty("next")
  private String next;

}
