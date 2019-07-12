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
  private String last; // todo danielw: data type should be URL or something equal

  @JsonProperty("next")
  private String next; // todo danielw: data type should be URL or something equal

}
